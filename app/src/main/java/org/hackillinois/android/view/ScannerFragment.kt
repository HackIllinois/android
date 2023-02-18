package org.hackillinois.android.view

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.budiyev.android.codescanner.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.zxing.BarcodeFormat
import kotlinx.android.synthetic.main.fragment_scanner.*
import kotlinx.android.synthetic.main.fragment_scanner.view.*
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.database.entity.Roles
import org.hackillinois.android.model.ScanStatus
import org.hackillinois.android.viewmodel.ScannerViewModel
// import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread

class ScannerFragment : Fragment() {
    val PERMISSIONS_REQUEST_ACCESS_CAMERA = 0

    lateinit var viewModel: ScannerViewModel

    private lateinit var eventId: String
    private lateinit var eventName: String

    private lateinit var codeScanner: CodeScanner

    private lateinit var staffChipGroup: ChipGroup

    private var userRoles: Roles? = null

    private var listOfEvents: MutableList<Event>? = null

    private var chipIdToEventId: MutableMap<Int, String> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val transitionInflater = TransitionInflater.from(requireContext())
        enterTransition = transitionInflater.inflateTransition(R.transition.slide_up_fragment)

        eventId = arguments?.getString(EVENT_ID_KEY) ?: ""
        eventName = arguments?.getString(EVENT_NAME_KEY) ?: ""

        viewModel = ViewModelProviders.of(this).get(ScannerViewModel::class.java).apply {
            init(eventName)
            lastScanStatus.observe(
                this@ScannerFragment,
                Observer {
                    if (userRoles != null && userRoles!!.isStaff()) {
                        displayStaffScanResult(it)
                    } else {
                        displayScanResult(it)
                    }
                }
            )
            roles.observe(
                this@ScannerFragment,
                Observer {
                    userRoles = it
                    showStaffChipGroup(it)
                }
            )

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_scanner, container, false)

        val closeButton = view.findViewById<ImageButton>(R.id.qrScannerClose)

        staffChipGroup = view.findViewById(R.id.staffChipGroup)

        viewModel.allEvents.observe(this) {
            // Filter out all the relevant details
            listOfEvents =
                it.events.filter { event -> event.eventType == "MEAL" || event.name == "Check-in"}.toMutableList()

            // Move the check-in to the first index
            val index = listOfEvents!!.indexOfFirst { event -> event.name == "Check-in" }
            val event = listOfEvents!![index]
            listOfEvents!!.removeAt(index)
            listOfEvents!!.add(0, event)

            var firstChipId = 0

            // Go through all the events and add a chip for it
            for ((index, event) in listOfEvents!!.withIndex()) {
                Log.i("Events", "${event.name}: ${event.eventType}")
                val chip = inflater.inflate(R.layout.staff_scanner_chip, staffChipGroup, false) as Chip
                chip.text = event.name
                val chipId = ViewCompat.generateViewId()
                if (index == 0) firstChipId = chipId
                chip.id = chipId
                chipIdToEventId[chipId] = event.id
                staffChipGroup.addView(chip)
            }

            Log.i("Events", "Number of events: ${listOfEvents!!.size}")

            // Select the first chipId
            staffChipGroup.check(firstChipId)
        }

        closeButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStackImmediate()
        }

        context?.let { context ->
            codeScanner = CodeScanner(context, view.codeScannerView).apply {
                camera = CodeScanner.CAMERA_BACK
                formats = listOf(BarcodeFormat.QR_CODE)
                autoFocusMode = AutoFocusMode.SAFE
                scanMode = ScanMode.SINGLE
                isAutoFocusEnabled = true
                isFlashEnabled = false
                decodeCallback = DecodeCallback {
                    if (userRoles != null && userRoles!!.isStaff()) {
                        val userString = getUserIdFromQrString(it.text)
                        Log.d("USER QR CODE", userString)
                        viewModel.checkUserIntoEventAsStaff(userString, getStaffCheckInEventId())
                    } else {
                        val eventId: String = getEventCodeFromQrString(it.text)
                        Log.d("EVENT CODE STRING", it.toString())
                        viewModel.scanQrToCheckIn(eventId)
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

    private fun getUserIdFromQrString(qrString: String): String {
        val splitOnEquals = qrString.split("=")
        return splitOnEquals.last()
    }
    private fun getEventCodeFromQrString(qrString: String): String {
        val splitOnEquals = qrString.split("=")
        return splitOnEquals.first()
    }

    private fun displayStaffScanResult(lastScanStatus: ScanStatus?) = lastScanStatus?.let {
        val responseString = when (it.userMessage) {
            "Success" -> "Success! The attendant has the following dietary restrictions: ${it.dietary}"
            "InvalidEventId" -> "The event code doesn't seem to be correct. Try selecting the event again or select another event"
            "BadUserToken" -> "The QR code may have expired or might be invalid. Please refresh the QR code and try again"
            "AlreadyCheckedIn" -> "Looks like the attendant is already checked in."
            else -> "Something isn't quite right."
        }
        // make toast from response
        Log.d("SCAN STATUS RESULT", responseString)
        if (activity != null) {
            AlertDialog.Builder(activity!!)
                .setMessage(responseString)
                .setPositiveButton("OK", DialogInterface.OnClickListener{ dialog, id -> })
                .create()
                .show()
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
        Log.d("SCAN STATUS RESULT", responseString)
        val toast = Toast.makeText(context, responseString, Toast.LENGTH_LONG)
        toast.show()
    }

    private fun showStaffChipGroup(it: Roles?) = it?.let {
        staffChipGroup.visibility = if (it.isStaff()) View.VISIBLE else View.GONE
    }

    private fun getStaffCheckInEventId(): String {
        return chipIdToEventId[staffChipGroup.checkedChipId] ?: "0b8ea2a94ba4224c075f016256fbddfa"
    }

    private fun updateOverrideSwitchVisibility(it: Roles?) = it?.let {
//        staffOverrideSwitch.visibility = if (it.isAdmin()) View.VISIBLE else View.GONE
    }

    private fun hideStatusTextVisibility(views: List<View>) {
        for (view in views) {
            view.visibility = View.INVISIBLE
        }
    }

    companion object {
        val EVENT_ID_KEY = "event_id"
        val EVENT_NAME_KEY = "event_name"

        fun newInstance(eventId: String, eventName: String): ScannerFragment {
            val fragment = ScannerFragment()
            val args = Bundle().apply {
                putString(EVENT_ID_KEY, eventId)
                putString(EVENT_NAME_KEY, eventName)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
