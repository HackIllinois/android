package org.hackillinois.android.common

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import java.util.*

class QRUtilities {
    companion object {
        fun generateQRCode(text: String, width: Int, height: Int, clearColor: Int, solidColor: Int): Bitmap {
            val pixels = IntArray(width * height)

            val multiFormatWriter = MultiFormatWriter()
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
            hints[EncodeHintType.MARGIN] = 0

            try {
                val bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints)

                for (x in 0 until width) {
                    for (y in 0 until height) {
                        pixels[y * width + x] = if (bitMatrix.get(x, y)) solidColor else clearColor
                    }
                }
            } catch (e: WriterException) {
                e.printStackTrace()
            }

            return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
        }
    }
}
