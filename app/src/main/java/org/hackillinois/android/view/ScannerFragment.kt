package org.hackillinois.android.view

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.budiyev.android.codescanner.*
import com.google.zxing.BarcodeFormat
import kotlinx.android.synthetic.main.fragment_scanner.*
import kotlinx.android.synthetic.main.fragment_scanner.view.*
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.model.ScanStatus
import org.hackillinois.android.viewmodel.ScannerViewModel


class ScannerFragment : Fragment() {
    val PERMISSIONS_REQUEST_ACCESS_CAMERA = 0
    private val CHECK_IN_TEXT = "Check In"

    lateinit var viewModel: ScannerViewModel

    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ScannerViewModel::class.java).apply {
            init()
            eventsListLiveData.observe(this@ScannerFragment, Observer { updateEventList(it) })
            lastScanStatus.observe(this@ScannerFragment, Observer { displayScanResult(it) })
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
                    val event = view.eventListView.selectedItem.toString()
                    val userId: String = getUserIdFromQrString(it.text)
                    viewModel.checkUserIntoEvent(event, userId, view.staffOverrideSwitch.isChecked)
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(context, "Sending request to API. Please wait for a response", Toast.LENGTH_LONG).show()
                    }
                }
                errorCallback = ErrorCallback {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(context, "Camera initialization error: ${it.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }

            view.codeScannerView.setOnClickListener { codeScanner.startPreview() }

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

    private fun getUserIdFromQrString(qrString: String): String {
        val splitOnEquals = qrString.split("=")
        return splitOnEquals.last()
    }

    private fun displayScanResult(lastScanStatus: ScanStatus?) = lastScanStatus?.let {
        val message = when (it.lastScanWasSuccessful) {
            true -> {
                staffOverrideSwitch.isChecked = false
                "Success: ${lastScanStatus.userId}"
            }
            false -> "User not registered, or already scanned in for event."
        }

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun updateOverrideSwitchVisibility(it: Boolean?) {
        staffOverrideSwitch.visibility = when (it) {
            true -> AdapterView.VISIBLE
            false -> AdapterView.GONE
            null -> AdapterView.GONE
        }
    }

    private fun updateEventList(eventList: List<Event>?) = eventList?.let { eventList ->
        var eventNameList: MutableList<String> = eventList.map { it.name }.toMutableList()
        eventNameList.add(CHECK_IN_TEXT)

        var spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(context,
                android.R.layout.simple_dropdown_item_1line, eventNameList)

        eventListView.adapter = spinnerAdapter
    }
}