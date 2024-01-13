package org.hackillinois.android.view.scanner

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
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
import org.hackillinois.android.database.entity.EventCode
import org.hackillinois.android.database.entity.MeetingEventId
import org.hackillinois.android.database.entity.Roles
import org.hackillinois.android.database.entity.UserEventPair
import org.hackillinois.android.model.profile.ProfilePoints
import org.hackillinois.android.model.scanner.ScanStatus
import org.hackillinois.android.model.shop.ItemInstance
import org.hackillinois.android.viewmodel.ScannerViewModel

class ScannerFragment : Fragment(), SimpleScanDialogFragment.OnSimpleOKButtonSelected, IconScanDialogFragment.OnIconOKButtonSelected {
    val PERMISSIONS_REQUEST_ACCESS_CAMERA = 0

    lateinit var viewModel: ScannerViewModel

    private var scanKey: String = ""
    private var userRoles: Roles? = null
    private var listOfEvents: MutableList<Event>? = null
    private var chipIdToEventId: MutableMap<Int, String> = mutableMapOf()

    private lateinit var codeScanner: CodeScanner
    private var alertDialog: AlertDialog? = null
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
                        displayAttendeeScanResult(it)
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
                    Log.d("QR", it.text)
                    if (userRoles != null && userRoles!!.isStaff()) {
                        // STAFF -> handle scanning for meeting attendance or attendee check in
                        when (scanKey) {
                            "meeting-attendance" -> {
                                val eventId: String = it.text
                                viewModel.submitMeetingAttendance(MeetingEventId(eventId))
                            }
                            "attendee-check-in" -> {
                                // todo
                                val userId = decodeUserId(it.text)
                                val eventId = getChipEventId()
                                viewModel.checkInAttendee(UserEventPair(userId, eventId))
                            }
                            "add-points" -> {
                                // todo
                                val userId = decodeUserId(it.text)
                                showTextInputDialog(userId)
                            }
                            else -> {
                                displayToast(R.string.something_went_wrong_message)
                                closeScannerPage()
                            }
                        }
                    } else {
                        // ATTENDEE -> handle event self check in, mentor check in, and point shop scan
                        when (scanKey) {
                            "event-check-in" -> {
                                // todo
                                val eventId: String = it.text
                                viewModel.checkInEvent(EventCode(eventId))
                            }
                            "mentor-check-in" -> {
                                // todo
                                viewModel.checkInMentor()
                            }
                            "point-shop" -> {
                                val itemInstance = decodeItemInfo(it.text)
                                viewModel.purchaseItem(itemInstance)
                            }
                            else -> {
                                displayToast(R.string.something_went_wrong_message)
                                closeScannerPage()
                            }
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
                "add-points" -> chipTag.text = "Add Points"
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

    private fun decodeUserId(qrString: String): String {
        val splitOnEquals = qrString.split("=")
        return splitOnEquals.last()
    }

    private fun decodeItemInfo(qrString: String): ItemInstance {
        val qrStringTrimmed = qrString.replace("\"", "")
        val uri = Uri.parse(qrStringTrimmed)
        val itemId = uri.getQueryParameter("itemId") ?: "null"
        val instance = uri.getQueryParameter("instance") ?: "null"
        return ItemInstance(itemId, instance)
    }

    private fun getChipEventId(): String {
        return chipIdToEventId[chipGroup.checkedChipId] ?: "0b8ea2a94ba4224c075f016256fbddfa" // default check-in TODO: update for 2024
    }

    private fun showTextInputDialog(userId: String) {
        Handler(Looper.getMainLooper()).post {
            val builder = AlertDialog.Builder(context)
            val dialogView: View = layoutInflater.inflate(R.layout.dialog_input, null)
            val editText = dialogView.findViewById<EditText>(R.id.editText)

            builder.setView(dialogView)
                .setTitle("Give attendee points")
                .setPositiveButton("OK") { dialog, id ->
                    // Retrieve the input text
                    val userInput = editText.text.toString()
                    parseAndAddPoints(userId, userInput)
                }
                .setCancelable(false)
                .setNegativeButton("Cancel") { dialog, id ->
                    dialog.dismiss()
                    codeScanner.startPreview()
                }

            val alertDialog = builder.create()
            alertDialog.show()
            val floralMauve = ContextCompat.getColor(requireContext(), R.color.floralMauve)
            val gray = ContextCompat.getColor(requireContext(), R.color.gray)
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(floralMauve)
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(gray)
        }
    }

    private fun parseAndAddPoints(userId: String, userInput: String) {
        val trimmedInput = userInput.trim()
        var points = 0
        try {
            points = trimmedInput.toInt()
            viewModel.giveAttendeePoints(ProfilePoints(userId, points))
        } catch (e: Exception) {
            displayToast(R.string.user_input_points_message)
            codeScanner.startPreview()
        }
    }

    private fun displayStaffScanResult(lastScanStatus: ScanStatus?) = lastScanStatus?.let {
        if (lastScanStatus.success) {
            showSimpleDialogFragment("Success!", lastScanStatus.userMessage)
        } else {
            showSimpleDialogFragment("Error", lastScanStatus.userMessage)
        }
    }

    private fun displayAttendeeScanResult(lastScanStatus: ScanStatus?) = lastScanStatus?.let {
        if (lastScanStatus.success) {
            when (scanKey) {
                "point-shop" -> {
                    showIconDialogFragment("prize", lastScanStatus.userMessage)
                }
                else -> {
                    showIconDialogFragment("points", lastScanStatus.userMessage)
                }
            }
        } else {
            showSimpleDialogFragment("Error", lastScanStatus.userMessage)
        }
    }

    private fun showIconDialogFragment(iconType: String, subtitle: String) {
        if (activity != null) {
            // create arguments bundle to pass to IconScanDialogFragment
            val args = Bundle()
            args.putString("KEY_ICON_TYPE", iconType)
            args.putString("KEY_SUBTITLE", subtitle)

            // create and show instance of IconScanDialogFragment
            val fragmentManager: FragmentManager = parentFragmentManager
            val dialog = IconScanDialogFragment()
            dialog.arguments = args
            dialog.setIconOKButtonListener(this)
            dialog.show(fragmentManager, "IconScanDialogFragment")
        }
    }

    private fun showSimpleDialogFragment(title: String, subtitle: String) {
        if (activity != null) {
            // create arguments bundle to pass to IconScanDialogFragment
            val args = Bundle()
            args.putString("KEY_TITLE", title)
            args.putString("KEY_SUBTITLE", subtitle)

            // create and show instance of IconScanDialogFragment
            val fragmentManager: FragmentManager = parentFragmentManager
            val dialog = SimpleScanDialogFragment()
            dialog.arguments = args
            dialog.setSimpleOKButtonListener(this)
            dialog.show(fragmentManager, "SimpleScanDialogFragment")
        }
    }

    private fun displayToast(message_string_resource: Int) {
        Handler(Looper.getMainLooper()).post {
            val toast = Toast.makeText(context, message_string_resource, Toast.LENGTH_LONG)
            toast.show()
        }
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
