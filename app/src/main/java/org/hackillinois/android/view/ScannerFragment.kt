package org.hackillinois.android.view

import android.Manifest
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.budiyev.android.codescanner.*
import com.google.zxing.BarcodeFormat
import kotlinx.android.synthetic.main.fragment_scanner.*
import kotlinx.android.synthetic.main.fragment_scanner.view.*
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Roles
import org.hackillinois.android.model.ScanStatus
import org.hackillinois.android.viewmodel.ScannerViewModel

class ScannerFragment : Fragment() {
    val PERMISSIONS_REQUEST_ACCESS_CAMERA = 0

    lateinit var viewModel: ScannerViewModel

    private lateinit var eventId: String
    private lateinit var eventName: String

    private lateinit var codeScanner: CodeScanner

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

        context?.let { context ->
            codeScanner = CodeScanner(context, view.codeScannerView).apply {
                camera = CodeScanner.CAMERA_BACK
                formats = listOf(BarcodeFormat.QR_CODE)
                autoFocusMode = AutoFocusMode.SAFE
                scanMode = ScanMode.SINGLE
                isAutoFocusEnabled = true
                isFlashEnabled = false
                decodeCallback = DecodeCallback {
                    val userId: String = getUserIdFromQrString(it.text)
                    val eventId: String = getCodeFromEvent(it.text)
//                    viewModel.checkUserIntoEvent(eventId, userId, view.staffOverrideSwitch.isChecked)
                    viewModel.scanQrToCheckIn(eventId, userId)
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

        // TESTING FUNCTION
    private fun getCodeFromEvent(text: String?): String {
        return "428326"
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

    private fun displayScanResult(lastScanStatus: ScanStatus?) = lastScanStatus?.let {
        val message = when (it.lastScanWasSuccessful) {
            true -> {
                staffOverrideSwitch.isChecked = false
                "Success: ${lastScanStatus.userId}"
            }
            false -> lastScanStatus.userMessage
        }

        view?.let {
            Snackbar.make(it.rootView, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun updateOverrideSwitchVisibility(it: Roles?) = it?.let {
        staffOverrideSwitch.visibility = if (it.isAdmin()) View.VISIBLE else View.GONE
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