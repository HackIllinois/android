package org.hackillinois.android.view

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import kotlinx.android.synthetic.main.fragment_profile.*

import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Attendee
import org.hackillinois.android.database.entity.QR
import org.hackillinois.android.database.entity.User
import org.hackillinois.android.viewmodel.ProfileViewModel

import java.util.EnumMap

class ProfileFragment : Fragment() {
    private var qrImageView: ImageView? = null
    private var nameTextView: TextView? = null
    private var dietTextView: TextView? = null

    private var universityLabel: TextView? = null
    private var universityTextView: TextView? = null
    private var majorLabel: TextView? = null
    private var majorTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        viewModel.init()

        viewModel.qr.observe(this, Observer { qr -> updateQrView(qr) })
        viewModel.user.observe(this, Observer { user -> updateUserInformation(user) })
        viewModel.attendee.observe(this, Observer { attendee -> updateAttendeeInformation(attendee) })
    }

    private fun updateQrView(qr: QR?) = qr?.let {
        val text = qr.qrInfo
        val bitmap = generateQR(text)
        qrImageView?.setImageBitmap(bitmap)
    }

    private fun updateUserInformation(user: User?) = user?.let {
        nameTextView?.text = user.getFullName()
    }

    private fun updateAttendeeInformation(attendee: Attendee?) {
        attendee?.let {
            val diet = attendee.getDietAsString()
            if (diet != null) {
                dietTextView?.text = diet
                dietTextView?.visibility = View.VISIBLE
            } else {
                dietTextView?.visibility = View.INVISIBLE
            }
            universityTextView?.text = attendee.school
            majorTextView?.text = attendee.major
        }

        val visibility = if (attendee != null) View.VISIBLE else View.INVISIBLE
        dietTextView?.visibility = visibility
        universityLabel?.visibility = visibility
        majorLabel?.visibility = visibility
    }

    private fun generateQR(text: String): Bitmap {
        val width = qrImageView?.width ?: 0
        val height = qrImageView?.height ?: 0
        val pixels = IntArray(width * height)

        val multiFormatWriter = MultiFormatWriter()
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.MARGIN] = 0

        try {
            val bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints)

            val CLEAR = Color.WHITE
            val SOLID = resources.getColor(R.color.colorPrimary)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    pixels[y * width + x] = if (bitMatrix.get(x, y)) SOLID else CLEAR
                }
            }
        } catch (e: WriterException) {
            e.printStackTrace()
        }

        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        qrImageView = view.findViewById(R.id.qr)
        nameTextView = view.findViewById(R.id.name)
        dietTextView = view.findViewById(R.id.dietaryRestrictions)

        universityLabel = view.findViewById(R.id.universityTitle)
        universityTextView = view.findViewById(R.id.university)
        majorLabel = view.findViewById(R.id.majorTitle)
        majorTextView = view.findViewById(R.id.major)

        return view
    }
}
