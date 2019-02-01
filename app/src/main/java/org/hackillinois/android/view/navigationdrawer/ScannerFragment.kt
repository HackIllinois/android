package org.hackillinois.android.view.navigationdrawer

import android.Manifest
import android.R
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import kotlinx.android.synthetic.main.fragment_scanner.*
import org.hackillinois.android.viewmodel.ScannerViewModel


class ScannerFragment : Fragment() {
    var lastScannedUser: String = ""
    val PERMISSIONS_REQUEST_ACCESS_CAMERA = 0
    lateinit var viewModel: ScannerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ScannerViewModel::class.java)
        viewModel.eventsListLiveData.observe(this, Observer { updateEventList(it) })

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
     * Event handler on successful scan.
     */
    private var onQrCodeScan: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            // Prevent duplicate scans
            if (result.text == null || result.text == lastScannedUser) {
                return
            }

            lastScannedUser = result.text

            TODO("Get selected event from the Spinner")

            TODO("Make API call to add user to event, differentiating between check-in" +
                    "and other events." +
                    "On successful API request, show the toast." +
                    "QR scanner is set-up to continuously scan QRs, but can be made to wait until" +
                    "after the last network request was completed, before attempting to scan.")

            Toast.makeText(context, lastScannedUser, Toast.LENGTH_LONG)
        }

        override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
            // Not required, therefore not implementing
        }
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
