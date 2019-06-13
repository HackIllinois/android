package org.hackillinois.android.view

import android.Manifest
import android.R
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import kotlinx.android.synthetic.main.fragment_scanner.*
import kotlinx.android.synthetic.main.fragment_scanner.view.*
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.model.ScanStatus
import org.hackillinois.android.model.checkin.CheckIn
import org.hackillinois.android.model.event.UserEventPair
import org.hackillinois.android.viewmodel.ScannerViewModel
import android.app.AlertDialog


class ScannerFragment : Fragment() {
    val INITIAL_SCANNED_USERID = ""
    val PERMISSIONS_REQUEST_ACCESS_CAMERA = 0
    val CHECK_IN_TEXT = "Check In"

    var lastSuccessfullyScannedUser: String = INITIAL_SCANNED_USERID
    var lastEventScannedFor: String = ""

    lateinit var viewModel: ScannerViewModel

    lateinit var handler: Handler

    private var shouldParseScan = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ScannerViewModel::class.java)
        viewModel.init()
        viewModel.eventsListLiveData.observe(this, Observer { updateEventList(it) })

        // We will only observe the change in lastScanWasSuccessful, as if that changes,
        // so will the scanned userId
        viewModel.lastScanStatus.observe(this, Observer { processLastScan(it) })

        viewModel.shouldDisplayOverrideSwitch.observe(this, Observer { updateOverrideSwitchVisibility(it) })
    }

    /**
     * Make the switch invisible when not relevant to the event.
     */
    private fun updateOverrideSwitchVisibility(it: Boolean?) {
        staffOverrideSwitch.visibility = when (it) {
            true -> AdapterView.VISIBLE
            false -> AdapterView.GONE
            null -> AdapterView.GONE
        }
    }

    /**
     * Called when scanner scans something.
     */
    private var onQrCodeScan: BarcodeCallback = object : BarcodeCallback {
        override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
        }

        override fun barcodeResult(result: BarcodeResult) {
            if (shouldParseScan) {
                // stop parsing scans until we have finished this one!
                shouldParseScan = false

                val currentEvent = eventListView.selectedItem.toString()

                // Prevent duplicate scans
                if (getUserIdFromQrString(result.text) == lastSuccessfullyScannedUser && currentEvent == lastEventScannedFor) {
                    return
                }

                val userId: String = getUserIdFromQrString(result.text)

                lastEventScannedFor = eventListView.selectedItem.toString()

                // Check-in is a special event in the HackIllinois API
                if (lastEventScannedFor == CHECK_IN_TEXT) {
                    val staffOverride = staffOverrideSwitch.isChecked
                    val hasCheckedIn = true
                    val hasPickedUpSwag = true
                    val checkIn = CheckIn(userId, staffOverride, hasCheckedIn, hasPickedUpSwag)

                    viewModel.checkInUser(checkIn)

                } else {
                    val userEventPair = UserEventPair(lastEventScannedFor, userId)
                    viewModel.markUserAsAttendingEvent(userEventPair)
                }
            }
        }
    }

    /**
     * A callback that is called when the status of the last scan request changes.
     */
    fun processLastScan(lastScanStatus: ScanStatus?) {

        when (lastScanStatus?.lastScanWasSuccessful) {
            false -> {
                AlertDialog.Builder(context).apply {
                    setMessage("User not registered, or already scanned in for event.")
                    setPositiveButton("Ok") { dialog, _ ->
                        shouldParseScan = true
                        dialog.cancel()
                    }
                    create()
                }.show()
            }
            true -> {
                lastSuccessfullyScannedUser = lastScanStatus.userId

                AlertDialog.Builder(context).apply {
                    setMessage("Success: $lastSuccessfullyScannedUser")
                    setPositiveButton("Ok") { dialog, _ ->
                        shouldParseScan = true
                        dialog.cancel()
                    }
                    create()
                }.show()
                staffOverrideSwitch.isChecked = false
            }
            null -> {
                shouldParseScan = true
                return
            }
        }

    }

    /**
     * Extracts the userId from the HackIllinois QR String URI.
     * Given an example URI "hackillinois://user?userid=github0000001"
     * it is enough to grab everything after the last (and only) equals sign.
     *
     * If the URI becomes more complex, we can use actual URI decoders.
     * @return the userId from a QR String
     */
    private fun getUserIdFromQrString(qrString: String): String {
        val splitOnEquals = qrString.split("=")
        return splitOnEquals.last()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSIONS_REQUEST_ACCESS_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                qrScanner.decodeContinuous(onQrCodeScan)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(org.hackillinois.android.R.layout.fragment_scanner, container, false)

        view.eventListView.onItemSelectedListener = viewModel.onEventSelected

        // Ensure the Camera permission is granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && activity!!.checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA),
                    PERMISSIONS_REQUEST_ACCESS_CAMERA)
        } else {
            view.qrScanner.decodeContinuous(onQrCodeScan)
        }

        return view
    }

    /**
     * Takes a list of events, grabs all of the name fields, creates an adapter with the list of
     * names, and uses that to populate the Spinner.
     * @param eventList the list of events from the latest publish event (local DB / API call)
     */
    private fun updateEventList(eventList: List<Event>?) = eventList?.let { eventList ->
        var eventNameList: MutableList<String> = eventList.map { it.name }.toMutableList()
        // The special element in the list that allows check in to the event, as opposed to
        // event check-in.
        eventNameList.add(CHECK_IN_TEXT)

        var spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(context,
                R.layout.simple_dropdown_item_1line, eventNameList)

        eventListView.adapter = spinnerAdapter
    }

    override fun onResume() {
        super.onResume()
        qrScanner.resume()
    }

    override fun onPause() {
        super.onPause()
        qrScanner.pause()
    }

}
