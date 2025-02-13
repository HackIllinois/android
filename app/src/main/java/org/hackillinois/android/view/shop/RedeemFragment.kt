package org.hackillinois.android.view.shop

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hackillinois.android.App
import org.hackillinois.android.R
import org.hackillinois.android.common.QRUtilities.Companion.generateQRCode
import org.hackillinois.android.database.entity.QRResponse

class RedeemFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_point_shop_redeem, container, false)

        // Handle Back Button Click
        val backButton: View = view.findViewById(R.id.title_textview_back)
        backButton.bringToFront()
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack() // Go back to previous fragment
        }

//        fun fetchQRCode(qrImageView: ImageView) {
//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    val response: QRResponse = App.getAPI().getCartQRCode()
//                    val qrBitmap = generateQRCode(response.qrCode)
//
//                    requireActivity().runOnUiThread {
//                        qrImageView.setImageBitmap(qrBitmap)
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }
//
//        fun generateQRCode(text: String): Bitmap {
//            val size = 512 // QR code size
//            val bitMatrix: BitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size)
//            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
//
//            for (x in 0 until size) {
//                for (y in 0 until size) {
//                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
//                }
//            }
//
//            return bitmap
        return view
        }
    }
