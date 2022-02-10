package org.hackillinois.android.view

import android.Manifest
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.budiyev.android.codescanner.*
import com.google.zxing.BarcodeFormat
import kotlinx.android.synthetic.main.fragment_scanner.*
import kotlinx.android.synthetic.main.fragment_scanner.view.*
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.EventCheckInResponse
import org.hackillinois.android.database.entity.Roles
import org.hackillinois.android.model.ScanStatus
import org.hackillinois.android.viewmodel.ScannerViewModel

class ScannerFragment : Fragment() {
    val PERMISSIONS_REQUEST_ACCESS_CAMERA = 0

    lateinit var viewModel: ScannerViewModel

    private lateinit var eventId: String
    private lateinit var eventName: String

    private lateinit var codeScanner: CodeScanner

    private lateinit var scanStatusText: TextView
    private lateinit var  scannerEventTitleView: TextView
    private lateinit var scannerEventAttributesView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        eventId = arguments?.getString(EVENT_ID_KEY) ?: ""
        eventName = arguments?.getString(EVENT_NAME_KEY) ?: ""

        viewModel = ViewModelProviders.of(this).get(ScannerViewModel::class.java).apply {
            init(eventName)
            lastScanStatus.observe(this@ScannerFragment, Observer { displayScanResult(it) })
            roles.observe(this@ScannerFragment, Observer { updateOverrideSwitchVisibility(it) })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_scanner, container, false)
        val closeButton = view.findViewById<ImageButton>(R.id.qrScannerClose)

        scanStatusText = view.findViewById(R.id.successTextView)
        scannerEventTitleView = view.findViewById(R.id.scannerEventTitleView)
        scannerEventAttributesView = view.findViewById(R.id.scannerEventAttributesView)

        // hide the text of scan status, event title, and attributes programmatically
        hideStatusTextVisibility(listOf(scanStatusText, scannerEventTitleView, scannerEventAttributesView))

        closeButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStackImmediate();
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
                    val eventId: String = getUserIdFromQrString(it.text)
                    Log.d("EVENT CODE STRING", it.toString())
                    val response = viewModel.scanQrToCheckIn(eventId)
                    setScannerTextAttributes(response)
                }
                errorCallback = ErrorCallback {
                    Handler(Looper.getMainLooper()).post {
                        Snackbar.make(view.rootView, it.message.toString(), Snackbar.LENGTH_LONG).show()
                    }
                }
            }

            view.codeScannerView.setOnClickListener { codeScanner.startPreview() }

            // get camera permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSIONS_REQUEST_ACCESS_CAMERA)
            } else {
                codeScanner.startPreview()
            }
        }

        return view
    }

    private fun setScannerTextAttributes(response: EventCheckInResponse) {
        var responseString: String = ""
        when (response.status) {
            "Success" -> responseString = "Success! You received ${response.newPoints} points."
            "InvalidCode" -> responseString = "This code doesn't seem to be correct."
            "InvalidTime" -> responseString = "Make sure you have the right time."
            "AlreadyCheckedIn" -> responseString = "Looks like you're already checked in."
            else -> responseString = "Something isn't quite right."
        }

        if (responseString.equals("Success")) {
            scanStatusText.setTextColor(R.color.successColor)
        } else {
            scanStatusText.setTextColor(R.color.errorColor)
        }
        scanStatusText.text = responseString
        scannerEventTitleView.text = response.newPoints.toString()
        scannerEventAttributesView.text = response.totalPoints.toString()
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

    private fun displayScanResult(lastScanStatus: ScanStatus?) = lastScanStatus?.let {
        val message = when (it.lastScanWasSuccessful) {
            true -> {
//                staffOverrideSwitch.isChecked = false
                "Success: ${lastScanStatus.userId}"
            }
            false -> lastScanStatus.userMessage
        }


//        view?.let {
//            Snackbar.make(it.rootView, message, Snackbar.LENGTH_SHORT).show()
//        }
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