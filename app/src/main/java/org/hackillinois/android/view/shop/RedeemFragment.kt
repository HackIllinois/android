package org.hackillinois.android.view.shop

import RedeemViewModel
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import org.hackillinois.android.R
import java.util.EnumMap

class RedeemFragment : Fragment() {

    private lateinit var qrCodeImage: ImageView
    private lateinit var redeemViewModel: RedeemViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_point_shop_redeem, container, false)

        // Handle Back Button Click
        val backButton: View = view.findViewById(R.id.title_textview_back)
        backButton.bringToFront()
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack() // Go back to previous fragment
        }

        qrCodeImage = view.findViewById(R.id.qr_code_placeholder)

        // Initialize and observe the ViewModel
        redeemViewModel = ViewModelProvider(this).get(RedeemViewModel::class.java)
        redeemViewModel.qrCodeLiveData.observe(
            viewLifecycleOwner,
            Observer { qrString ->
                Log.d("RedeemFragment", "Updated QR Code: $qrString")
                updateQRView(qrString)
            }
        )

        Toast.makeText(requireContext(), "Go to the Point Shop front desk to redeem!", Toast.LENGTH_LONG).show()

        redeemViewModel.errorLiveData.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun updateQRView(qrString: String) {
        if (qrCodeImage.width > 0 && qrCodeImage.height > 0) {
            Log.d("RedeemFragment", "Generating QR with text: $qrString")
            val bitmap = generateQR(qrString)
            qrCodeImage.setImageBitmap(bitmap)
        }
    }

    private fun generateQR(text: String): Bitmap {
        val width = qrCodeImage.width
        val height = qrCodeImage.height

        val pixels = IntArray(width * height)
        val multiFormatWriter = MultiFormatWriter()
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.MARGIN] = 0

        try {
            val bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints)
            val clear = Color.WHITE
            val solid = Color.BLACK
            for (x in 0 until width) {
                for (y in 0 until height) {
                    pixels[y * width + x] = if (bitMatrix.get(x, y)) solid else clear
                }
            }
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }

    override fun onResume() {
        super.onResume()
        redeemViewModel.startAutoRefresh() // Resume QR refresh when fragment is visible
    }

    override fun onPause() {
        super.onPause()
        redeemViewModel.stopAutoRefresh() // Pause QR refresh when fragment is hidden
    }
}
