package org.hackillinois.android.view.navigationdrawer

import android.Manifest
import android.R
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import kotlinx.android.synthetic.main.fragment_scanner.*
import org.hackillinois.android.App
import org.hackillinois.android.model.CheckIn
import org.hackillinois.android.model.UserEventPair
import org.hackillinois.android.viewmodel.ScannerViewModel


class ScannerFragment : Fragment() {
    var lastSuccessfullyScannedUser: String = ""
    var lastScannedUser: String = ""
    val PERMISSIONS_REQUEST_ACCESS_CAMERA = 0
    lateinit var viewModel: ScannerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ScannerViewModel::class.java)
        viewModel.init()
        viewModel.eventsListLiveData.observe(this, Observer { updateEventList(it) })
    }

    /**
     * Event handler on successful scan.
     */
    private var onQrCodeScan: BarcodeCallback = object : BarcodeCallback {
        override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun barcodeResult(result: BarcodeResult) {
            // Prevent duplicate scans
            if (result.text == null || result.text == lastSuccessfullyScannedUser) {
                return
            }

            lastScannedUser = result.text

            // Event that user is being scanned in for
            var eventName: String = eventListView.selectedItem.toString()

            // User's ID
            var userId: String? = getUserIdFromQrString(lastScannedUser)

            Log.d("ScanEvent", eventName)

            try {
                // Check-in is a special event in the HackIllinois API
                if (eventName == "Check In") {
                    var override = true
                    var hasCheckedIn = true
                    var hasPickedUpSwag = true
                    var checkIn = CheckIn(userId!!, override, hasCheckedIn, hasPickedUpSwag)

                    val response = App.getAPI().checkInUser(checkIn).execute()

                    response?.let {
                        if (response.isSuccessful) {
                            lastSuccessfullyScannedUser = lastScannedUser
                            // Prevent duplicate scans
                            Toast.makeText(context, lastSuccessfullyScannedUser, Toast.LENGTH_LONG)
                                    .show()
                        } else {
                            Toast.makeText(context, "Error processing request.",
                                    Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    var userEventPair = UserEventPair(eventName, userId!!)
                    val response = App.getAPI()
                            .markUserAsAttendingEvent(userEventPair).execute()

                    response?.let {
                        if (response.isSuccessful) {
                            lastSuccessfullyScannedUser = lastScannedUser
                            // Prevent duplicate scans
                            Toast.makeText(context, lastSuccessfullyScannedUser, Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Error processing request.",
                                    Toast.LENGTH_LONG).show()
                        }
                    }
                }


            } catch (exception: Exception) {
                Log.e("AttendeeRepository", exception.message)
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
    private fun getUserIdFromQrString(qrString: String): String? {
        var splitOnEquals = qrString.split("=")
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
        return inflater.inflate(org.hackillinois.android.R.layout.fragment_scanner, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Ensure the Camera permission is granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && activity!!.checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA),
                    PERMISSIONS_REQUEST_ACCESS_CAMERA)
        } else {
            qrScanner.decodeContinuous(onQrCodeScan)
        }
    }

    /**
     * Takes a list of events, grabs all of the name fields, creates an adapter with the list of
     * names, and uses that to populate the Spinner.
     * @param eventList the list of events from the latest publish event (local DB / API call)
     */
    private fun updateEventList(eventList: List<org.hackillinois.android.database.entity.Event>?) {
        var eventNameList: List<String> = eventList!!.map { it.name }

        var spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(context, R.layout.simple_dropdown_item_1line, eventNameList)

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
