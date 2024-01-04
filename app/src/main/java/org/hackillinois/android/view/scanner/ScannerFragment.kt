package org.hackillinois.android.view.scanner

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.*
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.zxing.BarcodeFormat
import kotlinx.android.synthetic.main.fragment_scanner.view.*
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.database.entity.Roles
import org.hackillinois.android.model.scanner.ScanStatus
import org.hackillinois.android.viewmodel.ScannerViewModel

class ScannerFragment : Fragment(), SimpleScanDialogFragment.OnSimpleOKButtonSelected, IconScanDialogFragment.OnIconOKButtonSelected {
    val PERMISSIONS_REQUEST_ACCESS_CAMERA = 0

    lateinit var viewModel: ScannerViewModel

    private var scanKey: String = ""
    private var userRoles: Roles? = null
    private var listOfEvents: MutableList<Event>? = null
    private var chipIdToEventId: MutableMap<Int, String> = mutableMapOf()

    private lateinit var codeScanner: CodeScanner
    private lateinit var chipGroup: ChipGroup
    private lateinit var chipTag: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scanKey = arguments?.getString(SCAN_KEY) ?: "null"

        // handle reloading in app bar if back button is clicked
        activity?.onBackPressedDispatcher?.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    closeScannerPage()
                }
            },
        )

        viewModel = ViewModelProvider(this).get(ScannerViewModel::class.java).apply {
            init()
            lastScanStatus.observe(
                this@ScannerFragment,
                Observer {
                    if (userRoles != null && userRoles!!.isStaff()) {
                        displayStaffScanResult(it)
                    } else {
                        displayScanResult(it)
                    }
                },
            )
            roles.observe(
                this@ScannerFragment,
                Observer {
                    userRoles = it
                    setUpChipTag()
                    showCorrectChips(it)
                },
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_scanner, container, false)

        chipGroup = view.findViewById(R.id.chipGroup)
        chipTag = view.findViewById(R.id.chipTag_textView)

        viewModel.allEvents.observe(this) {
            // Filter out all the relevant details
            listOfEvents = it.events.filter { event -> event.displayOnStaffCheckIn == true }.toMutableList()
            setUpChipGroup(listOfEvents!!, inflater)
        }

        chipGroup.visibility = View.VISIBLE

        // handle the close button being clicked
        val closeButton = view.findViewById<ImageButton>(R.id.qrScannerClose)
        closeButton.setOnClickListener {
            closeScannerPage()
        }

        // create instance of the codeScanner
        context?.let { context ->
            codeScanner = CodeScanner(context, view.codeScannerView).apply {
                camera = CodeScanner.CAMERA_BACK
                formats = listOf(BarcodeFormat.QR_CODE)
                autoFocusMode = AutoFocusMode.SAFE
                scanMode = ScanMode.SINGLE
                isAutoFocusEnabled = true
                isFlashEnabled = false

                // handle logic when a QR code is scanned
                decodeCallback = DecodeCallback {
                    if (userRoles != null && userRoles!!.isStaff()) {
                        // STAFF -> handle scanning for meeting attendance, attendee check in, or staff check in (admin)
                        if (scanKey == "meeting-attendance") {
                            val eventId: String = it.text
                            viewModel.staffCheckInMeeting(eventId)
                        } else if (scanKey == "attendee-check-in") {
                            val userId = getUserIdFromQR(it.text)
                            val eventId = getChipEventId()
                            viewModel.staffCheckInAttendee(userId, eventId)
                        } else if (scanKey == "staff-check-in") {
                            // todo
                        } else {
                            closeScannerPage()
                            // todo error?
                        }
                    } else {
                        // ATTENDEE -> handle event self check in, mentor check in, and point shop scan
                        if (scanKey == "event-check-in") {
                            val eventId: String = it.text
                            viewModel.attendeeCheckInEvent(eventId)
                        } else if (scanKey == "mentor-check-in") {
                            // todo
                        } else if (scanKey == "point-shop") {
                            // todo
                        } else {
                            closeScannerPage()
                            // todo error?
                        }
                    }
                }
                errorCallback = ErrorCallback {
                    Handler(Looper.getMainLooper()).post {
                        val toast = Toast.makeText(context, it.message.toString(), Toast.LENGTH_LONG)
                        toast.show()
                    }
                }
            }

            view.codeScannerView.setOnClickListener { codeScanner.startPreview() }

            // get camera permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSIONS_REQUEST_ACCESS_CAMERA)
            } else {
                codeScanner.startPreview()
            }
        }

        return view
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSIONS_REQUEST_ACCESS_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                codeScanner.startPreview()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        super.onPause()
        codeScanner.releaseResources()
    }

    private fun setUpChipGroup(listOfEvents: MutableList<Event>, inflater: LayoutInflater) {
        // Move the check-in to the first index
        val index = listOfEvents.indexOfFirst { event -> event.name == "Check-in" }
        if (index >= 0) {
            val event = listOfEvents[index]
            listOfEvents.removeAt(index)
            listOfEvents.add(0, event)
        }

        var firstChipId = 0

        // Go through all the events and add a chip for it
        for ((idx, event) in listOfEvents.withIndex()) {
            val chip = inflater.inflate(R.layout.staff_scanner_chip, chipGroup, false) as Chip
            chip.text = event.name
            val chipId = ViewCompat.generateViewId()
            if (idx == 0) firstChipId = chipId
            chip.id = chipId
            chipIdToEventId[chipId] = event.eventId
            chipGroup.addView(chip)
        }

        // Select the first chipId
        chipGroup.check(firstChipId)
    }

    private fun setUpChipTag() {
        if (isStaff()) {
            when (scanKey) {
                "meeting-attendance" -> chipTag.text = "Meeting Attendance"
                "staff-check-in" -> chipTag.text = "Staff"
                else -> chipTag.text = ""
            }
        } else {
            when (scanKey) {
                "point-shop" -> chipTag.text = "Point Shop"
                "mentor-check-in" -> chipTag.text = "Mentor"
                else -> chipTag.text = "Event"
            }
        }
    }

    private fun showCorrectChips(it: Roles?) = it?.let {
        if (it.isStaff() && (scanKey == "attendee-check-in")) {
            chipGroup.visibility = View.VISIBLE
            chipTag.visibility = View.INVISIBLE
        } else {
            chipTag.visibility = View.VISIBLE
            chipGroup.visibility = View.INVISIBLE
        }
    }

    private fun getUserIdFromQR(qrString: String): String {
        val splitOnEquals = qrString.split("=")
        return splitOnEquals.last()
    }

    private fun getChipEventId(): String {
        return chipIdToEventId[chipGroup.checkedChipId] ?: "0b8ea2a94ba4224c075f016256fbddfa" // default check-in TODO: update for 2024
    }

    private fun displayStaffScanResult(lastScanStatus: ScanStatus?) = lastScanStatus?.let {
        val responseString = it.userMessage

        // make dialog from response
        if (activity != null) {
            // create arguments bundle to pass to SimpleScanDialogFragment
            val args = Bundle()
            args.putString("KEY_TITLE", "Error")
            args.putString("KEY_SUBTITLE", lastScanStatus.userMessage)

            // create and show instance of SimpleScanDialogFragment
            val fragmentManager: FragmentManager = parentFragmentManager
            val dialog = IconScanDialogFragment()
            dialog.arguments = args
            dialog.setIconOKButtonListener(this)
            dialog.show(fragmentManager, "IconScanDialogFragment")
        } else {
            val toast = Toast.makeText(context, responseString, Toast.LENGTH_LONG)
            toast.show()
        }
    }

    private fun displayScanResult(lastScanStatus: ScanStatus?) = lastScanStatus?.let {
        val responseString = when (lastScanStatus.userMessage) {
            "Success" -> "Success! You received ${lastScanStatus.points} points."
            "InvalidCode" -> "This code doesn't seem to be correct."
            "InvalidTime" -> "Make sure you have the right time."
            "AlreadyCheckedIn" -> "Looks like you're already checked in."
            else -> "Something isn't quite right."
        }
        // make toast from response
        val toast = Toast.makeText(context, responseString, Toast.LENGTH_LONG)
        toast.show()
    }

    private fun closeScannerPage() {
        // set bottom app bar visible again and pop scanner fragment from the backstack
        val appBar = activity!!.findViewById<BottomAppBar>(R.id.bottomAppBar)
        val scannerBtn = activity!!.findViewById<FloatingActionButton>(R.id.code_entry_fab)
        appBar.visibility = View.VISIBLE
        scannerBtn.visibility = View.VISIBLE
        activity?.supportFragmentManager?.popBackStackImmediate()
    }

    override fun continueScanningAfterSimpleDialog() {
        codeScanner.startPreview()
        // close scanner if was for meeting attendance?
    }

    override fun continueScanningAfterIconDialog() {
        codeScanner.startPreview()
    }

    companion object {
        val SCAN_KEY = "scan_key"

        fun newInstance(scan_key: String): ScannerFragment {
            val fragment = ScannerFragment()
            val args = Bundle().apply {
                putString(SCAN_KEY, scan_key)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private fun isStaff(): Boolean {
        val context = requireActivity().applicationContext
        val prefString = context.getString(R.string.authorization_pref_file_key)
        return context.getSharedPreferences(prefString, Context.MODE_PRIVATE).getString("provider", "") ?: "" == "google"
    }
}
