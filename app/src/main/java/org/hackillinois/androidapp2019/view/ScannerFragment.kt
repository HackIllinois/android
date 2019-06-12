package org.hackillinois.androidapp2019.view

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_scanner.*
import kotlinx.android.synthetic.main.fragment_scanner.view.*
import org.hackillinois.androidapp2019.database.entity.Event
import org.hackillinois.androidapp2019.viewmodel.ScannerViewModel
import android.widget.Toast
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.zxing.BarcodeFormat
import org.hackillinois.androidapp2019.R
import org.hackillinois.androidapp2019.model.ScanStatus

class ScannerFragment : Fragment() {
    val PERMISSIONS_REQUEST_ACCESS_CAMERA = 0
    val CHECK_IN_TEXT = "Check In"

    lateinit var viewModel: ScannerViewModel

    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ScannerViewModel::class.java).apply {
            init()
            //eventsListLiveData.observe(this@ScannerFragment, Observer { updateEventList(it) })
            lastScanStatus.observe(this@ScannerFragment, Observer { processLastScan(it) })
            shouldDisplayOverrideSwitch.observe(this@ScannerFragment, Observer { updateOverrideSwitchVisibility(it) })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_scanner, container, false)

        view.eventListView.onItemSelectedListener = viewModel.onEventSelected

        context?.let { context ->
            codeScanner = CodeScanner(context, view.codeScannerView).apply {
                camera = CodeScanner.CAMERA_BACK
                formats = listOf(BarcodeFormat.QR_CODE)
                autoFocusMode = AutoFocusMode.SAFE
                scanMode = ScanMode.SINGLE
                isAutoFocusEnabled = true
                isFlashEnabled = false
                decodeCallback = DecodeCallback {
                    Toast.makeText(context, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()

                }
                errorCallback = ErrorCallback {
                    Toast.makeText(context, "Camera initialization error: ${it.message}", Toast.LENGTH_LONG).show()
                }
            }

            codeScannerView.setOnClickListener { codeScanner.startPreview() }

            // get camera permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSIONS_REQUEST_ACCESS_CAMERA)
                } else {
                    codeScanner.startPreview()
                }
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
        codeScanner.releaseResources()
        super.onPause()
    }
//
//    /**
//     * Called when scanner scans something.
//     */
//    private var onQrCodeScan: BarcodeCallback = object : BarcodeCallback {
//        override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
//        }
//
//        override fun barcodeResult(result: BarcodeResult) {
//            if (shouldParseScan) {
//                // stop parsing scans until we have finished this one!
//                shouldParseScan = false
//
//                val currentEvent = eventListView.selectedItem.toString()
//
//                // Prevent duplicate scans
//                if (getUserIdFromQrString(result.text) == lastSuccessfullyScannedUser && currentEvent == lastEventScannedFor) {
//                    return
//                }
//
//                val userId: String = getUserIdFromQrString(result.text)
//
//                lastEventScannedFor = eventListView.selectedItem.toString()
//
//                // Check-in is a special event in the HackIllinois API
//                if (lastEventScannedFor == CHECK_IN_TEXT) {
//                    val staffOverride = staffOverrideSwitch.isChecked
//                    val hasCheckedIn = true
//                    val hasPickedUpSwag = true
//                    val checkIn = CheckIn(userId, staffOverride, hasCheckedIn, hasPickedUpSwag)
//
//                    viewModel.checkInUser(checkIn)
//
//                } else {
//                    val userEventPair = UserEventPair(lastEventScannedFor, userId)
//                    viewModel.markUserAsAttendingEvent(userEventPair)
//                }
//            }
//        }
//    }
//
    /**
     * A callback that is called when the status of the last scan request changes.
     */
    fun processLastScan(lastScanStatus: ScanStatus?) = lastScanStatus?.let {
        val message = when (it.lastScanWasSuccessful) {
            true -> {
                staffOverrideSwitch.isChecked = false
                "Success: ${lastScanStatus.userId}"
            }
            false -> "User not registered, or already scanned in for event."
        }

        context?.let {context ->
            AlertDialog.Builder(context).apply {
                setMessage(message)
                setPositiveButton("Ok") { dialog, _ ->
                    dialog.cancel()
                }
                create()
            }.show()
        }
    }

    /**
     * Extracts the userId from the HackIllinois QR String URI.
     * Given an example URI "hackillinois://user?userid=github0000001"
     */
    private fun getUserIdFromQrString(qrString: String): String {
        val splitOnEquals = qrString.split("=")
        return splitOnEquals.last()
    }

    private fun updateOverrideSwitchVisibility(it: Boolean?) {
        staffOverrideSwitch.visibility = when (it) {
            true -> AdapterView.VISIBLE
            false -> AdapterView.GONE
            null -> AdapterView.GONE
        }
    }

//    private fun updateEventList(eventList: List<Event>?) = eventList?.let { eventList ->
//        var eventNameList: MutableList<String> = eventList.map { it.name }.toMutableList()
//        // The special element in the list that allows check in to the event, as opposed to
//        // event check-in.
//        eventNameList.add(CHECK_IN_TEXT)
//
//        var spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(context,
//                R.layout.simple_dropdown_item_1line, eventNameList)
//
//        eventListView.adapter = spinnerAdapter
//    }
}
