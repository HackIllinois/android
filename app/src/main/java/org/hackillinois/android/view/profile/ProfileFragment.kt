package org.hackillinois.android.view.profile

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
// import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import kotlinx.android.synthetic.main.fragment_profile.*
// import com.bumptech.glide.Glide
// import com.bumptech.glide.load.engine.DiskCacheStrategy
// import com.bumptech.glide.load.resource.bitmap.CenterCrop
// import com.bumptech.glide.load.resource.bitmap.RoundedCorners
// import com.bumptech.glide.request.RequestOptions
import org.hackillinois.android.R
import org.hackillinois.android.common.JWTUtilities
import org.hackillinois.android.database.entity.Attendee
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.database.entity.QR
import org.hackillinois.android.view.MainActivity
import org.hackillinois.android.viewmodel.ProfileViewModel
import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var nameText: TextView
    private lateinit var pointsText: TextView
    private lateinit var qrCodeImage: ImageView
    private lateinit var tierText: TextView
    private lateinit var waveText: TextView
    private lateinit var noneText: TextView
    private lateinit var vegetarianText: TextView
    private lateinit var veganText: TextView
    private lateinit var dairyFreeText: TextView
    private lateinit var glutenFreeText: TextView
    private lateinit var otherText: TextView

    lateinit var front_anim: AnimatorSet
    lateinit var back_anim: AnimatorSet
    var isFront = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasLoggedIn() or (hasLoggedIn() and isStaff())) {
            return
        }
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        viewModel.init()
        viewModel.currentProfileLiveData.observe(this, Observer { updateProfileUI(it) })
        viewModel.qr.observe(this, Observer { updateQrView(it) })
        viewModel.attendee.observe(this) { updateDietaryRestrictions(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!hasLoggedIn() or (hasLoggedIn() and isStaff())) {
            val view = inflater.inflate(R.layout.fragment_profile_not_logged_in, container, false)
            val logoutButton = view.findViewById<Button>(R.id.logout_button)
            logoutButton.setOnClickListener {
                val mainActivity: MainActivity = requireActivity() as MainActivity
                mainActivity.logout()
            }
            return view
        }

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        nameText = view.findViewById(R.id.nameText)
        pointsText = view.findViewById(R.id.ptsText)
        tierText = view.findViewById(R.id.tierText)
        qrCodeImage = view.findViewById(R.id.qrCodeImage)
        waveText = view.findViewById(R.id.waveText)
        noneText = view.findViewById(R.id.noneText)
        vegetarianText = view.findViewById(R.id.vegetarianText)
        veganText = view.findViewById(R.id.veganText)
        dairyFreeText = view.findViewById(R.id.dairyFreeText)
        glutenFreeText = view.findViewById(R.id.glutenFreeText)
        otherText = view.findViewById(R.id.otherText)

        val logoutButton1 = view.findViewById<ImageButton>(R.id.logoutButton)
        logoutButton1.setOnClickListener {
            val mainActivity: MainActivity = requireActivity() as MainActivity
            mainActivity.logout()
        }

        // flip animation
        var scale = requireActivity().applicationContext.resources.displayMetrics.density
        val front = view.findViewById<ConstraintLayout>(R.id.ticket_front)
        val back = view.findViewById<ImageView>(R.id.ticket_back)
        front.cameraDistance = 8000 * scale
        back.cameraDistance = 8000 * scale
        front_anim =
            AnimatorInflater.loadAnimator(context, R.animator.front_animator) as AnimatorSet
        back_anim = AnimatorInflater.loadAnimator(context, R.animator.back_animator) as AnimatorSet

        val flipButton = view.findViewById<Button>(R.id.flipButton)
        flipButton.setOnClickListener {
            flipButton.setClickable(false)
            if (isFront) {
                front_anim.setTarget(front)
                back_anim.setTarget(back)
                front_anim.start()
                back_anim.start()
                isFront = false
            } else {
                front_anim.setTarget(back)
                back_anim.setTarget(front)
                back_anim.start()
                front_anim.start()
                isFront = true
            }
            Handler().postDelayed({ flipButton.setClickable(true) }, 1200)
        }
        return view
    }

    private fun updateDietaryRestrictions(attendee: Attendee?) = attendee?.let { it ->
        val dietary = it.dietary
        if (it.dietary.isEmpty() || (it.dietary.size == 1 && it.dietary[0] == "None")) {
            noneText.visibility = View.VISIBLE
        } else {
            for (diet in dietary) {
                when (diet) {
                    "Vegetarian" -> vegetarianText.visibility = View.VISIBLE
                    "Vegan" -> veganText.visibility = View.VISIBLE
                    "Lactose Intolerant" -> dairyFreeText.visibility = View.VISIBLE
                    "Gluten Free" -> glutenFreeText.visibility = View.VISIBLE
                    else -> otherText.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun updateProfileUI(profile: Profile?) = profile?.let { it ->
        val currPoints = it.points
        pointsText.text = "$currPoints pts"
        nameText.text = "${it.firstName} ${it.lastName}"
        waveText.text = "Wave ${it.foodWave}"

        when {
            currPoints < 100 -> {
                tierText.text = "Clown Tier"
            }
            currPoints < 900 -> {
                tierText.text = "Juggler Tier"
            }
            else -> {
                tierText.text = "Acrobat Tier"
            }
        }
        /** set pfp programmatically based on threshold -- api call to
         * profile/tier/threshold/ returns
         *[
         *    {
         *        "name": "flour",
         *        "threshold": 0
         *    },
         *    {
         *        "name": "cookie",
         *        "threshold": 500
         *    },
         *    {
         *        "name": "cake",
         *        "threshold": 800
         *    }
         *]
         */

//        try {
//            context?.let { it1 ->
//                Glide.with(it1)
//                    .load(it.avatarUrl)
//                    .apply(
//                        RequestOptions()
//                            .transform(CenterCrop(), RoundedCorners(16))
//                            .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    )
//                    .into(ticketImage)
//            }
//        } catch (e: Exception) {
//            Log.e("Load profile image", e.toString())
//        }
    }

    private fun updateQrView(qr: QR?) = qr?.let { it ->
        if (qrCodeImage.width > 0 && qrCodeImage.height > 0) {
            val text = qr.qrInfo
            val bitmap = generateQR(text)
            qrCodeImage?.setImageBitmap(bitmap)
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
            val bitMatrix =
                multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints)

            val clear = Color.TRANSPARENT
            val solid = Color.WHITE

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

    private fun hasLoggedIn(): Boolean {
        return JWTUtilities.readJWT(requireActivity().applicationContext) != JWTUtilities.DEFAULT_JWT
    }

    private fun isStaff(): Boolean {
        val context = requireActivity().applicationContext
        return context.getSharedPreferences(
            context.getString(R.string.authorization_pref_file_key),
            Context.MODE_PRIVATE
        ).getString("provider", "") ?: "" == "google"
    }
}
