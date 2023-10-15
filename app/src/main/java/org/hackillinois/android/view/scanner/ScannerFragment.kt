package org.hackillinois.android.view.scanner

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
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

class ScannerFragment : Fragment() {
    val PERMISSIONS_REQUEST_ACCESS_CAMERA = 0

    lateinit var viewModel: ScannerViewModel

    private var isMeetingAttendance: Boolean = false
    private var userRoles: Roles? = null
    private var listOfEvents: MutableList<Event>? = null
    private var chipIdToEventId: MutableMap<Int, String> = mutableMapOf()

    private lateinit var codeScanner: CodeScanner
    private var alertDialog: AlertDialog? = null
    private lateinit var staffChipGroup: ChipGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isMeetingAttendance = arguments?.getBoolean(IS_MEETING_ATTENDANCE_KEY) ?: false

        // handle reloading in app bar if back button is clicked
        activity?.onBackPressedDispatcher?.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // set bottom app bar visible again and pop scanner fragment from the backstack
                    val appBar = activity!!.findViewById<BottomAppBar>(R.id.bottomAppBar)
                    val scannerBtn = activity!!.findViewById<FloatingActionButton>(R.id.code_entry_fab)
                    appBar.visibility = View.VISIBLE
                    scannerBtn.visibility = View.VISIBLE
                    activity?.supportFragmentManager?.popBackStackImmediate()
                }
            },
        )

        viewModel = ViewModelProvider(this).get(ScannerViewModel::class.java).apply {
            init()
            lastScanStatus.observe(this@ScannerFragment) {
                if (userRoles != null && userRoles!!.isStaff()) {
                    displayStaffScanResult(it)
                } else {
                    displayScanResult(it)
                }
                codeScanner.startPreview()
            }
            roles.observe(
                this@ScannerFragment,
                Observer {
                    userRoles = it
                    showStaffChipGroup(it)
                },
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_scanner, container, false)

        val closeButton = view.findViewById<ImageButton>(R.id.qrScannerClose)

        staffChipGroup = view.findViewById(R.id.staffChipGroup)

        viewModel.allEvents.observe(this) {
            // Filter out all the relevant details
            listOfEvents = it.events.filter { event -> event.eventType == "MEAL" || event.name == "Check-in" }.toMutableList()

            // Move the check-in to the first index
            val index = listOfEvents!!.indexOfFirst { event -> event.name == "Check-in" }
            if (index >= 0) {
                val event = listOfEvents!![index]
                listOfEvents!!.removeAt(index)
                listOfEvents!!.add(0, event)
            }

            var firstChipId = 0

            // Go through all the events and add a chip for it
            if (isStaff()) {
                for ((index, event) in listOfEvents!!.withIndex()) {
                    val chip = inflater.inflate(R.layout.staff_scanner_chip, staffChipGroup, false) as Chip
                    chip.text = event.name
                    val chipId = ViewCompat.generateViewId()
                    if (index == 0) firstChipId = chipId
                    chip.id = chipId
                    chipIdToEventId[chipId] = event.id
                    staffChipGroup.addView(chip)
                }
            }

            // Select the first chipId
            staffChipGroup.check(firstChipId)
        }

        // handle reloading in app bar if close button is clicked
        closeButton.setOnClickListener {
            // set bottom app bar visible again and pop scanner fragment from the backstack
            val appBar = activity!!.findViewById<BottomAppBar>(R.id.bottomAppBar)
            val scannerBtn = activity!!.findViewById<FloatingActionButton>(R.id.code_entry_fab)
            appBar.visibility = View.VISIBLE
            scannerBtn.visibility = View.VISIBLE
            activity?.supportFragmentManager?.popBackStackImmediate()
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
                        // STAFF -> handle if QR is for meeting attendance or attendee check-in
                        if (isMeetingAttendance) {
                            val eventId: String = it.text
                            Log.d("QR CODE", eventId)
                            viewModel.staffCheckInMeeting(eventId)
                        } else {
                            /* TODO: Fix this?
                               I'm not sure that it's even getting the right event id for each chip,
                               hence the getStaffCheckInEventId() is just the check-in id
                            */
                            val userId = getUserIdFromQR(it.text)
                            viewModel.staffCheckInAttendee(userId, getStaffCheckInEventId())
                        }
                    } else {
                        // ATTENDEE -> handle event self check-in
                        val eventId: String = it.text
                        viewModel.attendeeCheckInEvent(eventId)
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

            // check camera permissions and request them if not already given
            val requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                    if (isGranted) {
                        codeScanner.startPreview()
                    } else {
                        val appBar = activity!!.findViewById<BottomAppBar>(R.id.bottomAppBar)
                        val scannerBtn = activity!!.findViewById<FloatingActionButton>(R.id.code_entry_fab)
                        appBar.visibility = View.VISIBLE
                        scannerBtn.visibility = View.VISIBLE
                        activity?.supportFragmentManager?.popBackStackImmediate()
                    }
                }

            when {
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                    codeScanner.startPreview()
                } else -> {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }

//            if (context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSIONS_REQUEST_ACCESS_CAMERA)
//            } else {
//                codeScanner.startPreview()
//            }
        }

        return view
    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (requestCode == PERMISSIONS_REQUEST_ACCESS_CAMERA) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                codeScanner.startPreview()
//            }
//        }
//    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        super.onPause()
        codeScanner.releaseResources()
    }

    private fun showStaffChipGroup(it: Roles?) = it?.let {
        staffChipGroup.visibility = if (it.isStaff() && !isMeetingAttendance) View.VISIBLE else View.INVISIBLE
    }

    private fun getUserIdFromQR(qrString: String): String {
        val splitOnEquals = qrString.split("=")
        return splitOnEquals.last()
    }

    private fun displayStaffScanResult(lastScanStatus: ScanStatus?) = lastScanStatus?.let {
        val responseString = when (it.userMessage) {
            "Success" -> {
                if (isMeetingAttendance) {
                    "Success! Your attendance has been recorded!"
                } else {
                    "Success! The attendant has the following dietary restrictions: ${it.dietary}"
                }
            }
            "InvalidEventId" -> "The event code doesn't seem to be correct. Try selecting the event again or select another event"
            "BadUserToken" -> "The QR code may have expired or might be invalid. Please refresh the QR code and try again"
            "AlreadyCheckedIn" -> "Looks like the attendant is already checked in."
            else -> {
                if (isMeetingAttendance) {
                    "Scan Failed. ${it.userMessage}"
                } else {
                    "Something isn't quite right."
                }
            }
        }
        // make dialog from response
        if (activity != null) {
            if (alertDialog == null) {
                val builder = AlertDialog.Builder(requireActivity())
                    .setMessage(responseString)
                    .setNegativeButton("OK") { dialog, id ->
                        dialog.dismiss()
                    }
                alertDialog = builder.create()
            }
            alertDialog!!.show()
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

    private fun getStaffCheckInEventId(): String {
        return chipIdToEventId[staffChipGroup.checkedChipId] ?: "0b8ea2a94ba4224c075f016256fbddfa"
    }

    companion object {
        val IS_MEETING_ATTENDANCE_KEY = "is_meeting_attendance_key"

        fun newInstance(isMeetingAttendance: Boolean): ScannerFragment {
            val fragment = ScannerFragment()
            val args = Bundle().apply {
                putBoolean(IS_MEETING_ATTENDANCE_KEY, isMeetingAttendance)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private fun isStaff(): Boolean {
        val context = requireActivity().applicationContext
        return (
            context.getSharedPreferences(
                context.getString(R.string.authorization_pref_file_key),
                Context.MODE_PRIVATE,
            ).getString("provider", "") ?: ""
            ) == "google"
    }
}
