package org.hackillinois.android.view.profile

import android.animation.AnimatorSet
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import kotlinx.android.synthetic.main.fragment_profile_2024_redone.avatarImage2
import org.hackillinois.android.R
import org.hackillinois.android.common.ImageLoadingUtilities
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
//  private lateinit var pointsText: TextView
    private lateinit var qrCodeImage: ImageView
    private lateinit var avatarImage: ImageView
    private lateinit var leftProfileIcon: ImageView
    private lateinit var rightProfileIcon: ImageView
    // private lateinit var tierText: TextView

    // Attendee only elements
    private lateinit var waveText: TextView
    private lateinit var attendeeTypeText: TextView
    private lateinit var placementBannerImage: ImageView
    private lateinit var yourRankingText: TextView
    private lateinit var leaderboardIcon: ImageView
    private lateinit var rankingPlacementText: TextView


//    private lateinit var noneText: TextView
//    private lateinit var vegetarianText: TextView
//    private lateinit var veganText: TextView
//    private lateinit var dairyFreeText: TextView
//    private lateinit var glutenFreeText: TextView
//    private lateinit var otherText: TextView

    lateinit var front_anim: AnimatorSet
    lateinit var back_anim: AnimatorSet
    var isFront = true

    var staff = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!hasLoggedIn()) {
            return
        }
        // View Model Set Up
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        viewModel.init()
        viewModel.currentProfileLiveData.observe(this, Observer { updateProfileUI(it) })
        viewModel.qr.observe(this, Observer { updateQrView(it) })
        viewModel.attendee.observe(this) { }

        staff = isStaff()

//        if (!hasLoggedIn() or (hasLoggedIn() and staff)) {
//            return
//        }
        // view model initialization

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        if (!hasLoggedIn() or (hasLoggedIn() and staff)) {
//            val view = inflater.inflate(R.layout.fragment_profile_not_logged_in, container, false)
//            val logoutButton = view.findViewById<Button>(R.id.logout_button)
//            logoutButton.setOnClickListener {
//                val mainActivity: MainActivity = requireActivity() as MainActivity
//                mainActivity.logout()
//            }
//            return view
//        }
        if (!hasLoggedIn()) {
            val view = inflater.inflate(R.layout.fragment_profile_not_logged_in, container, false)
            val logoutButton = view.findViewById<Button>(R.id.logout_button)
            logoutButton.setOnClickListener {
                val mainActivity: MainActivity = requireActivity() as MainActivity
                mainActivity.logout()
            }
            return view
        }
        val view = inflater.inflate(R.layout.fragment_profile_2024_redone, container, false)
        nameText = view.findViewById(R.id.nameText2)
        qrCodeImage = view.findViewById(R.id.qrCodeImage2024)
        avatarImage = view.findViewById(R.id.avatarImage2)
        leftProfileIcon = view.findViewById(R.id.leftProfileLogo2)
        rightProfileIcon = view.findViewById(R.id.rightProfileLogo2)

        // do view creation here if staff, else do use
        waveText = view.findViewById(R.id.waveText2)
        attendeeTypeText = view.findViewById(R.id.attendeeTypeText2)
        placementBannerImage = view.findViewById(R.id.placementBannerImageView)
        yourRankingText = view.findViewById(R.id.rankingTextView2)
        leaderboardIcon = view.findViewById(R.id.rankingImageView2)
        rankingPlacementText = view.findViewById(R.id.rankingPlacementTextView2)

        if (staff) {
            waveText.visibility = View.GONE
            attendeeTypeText.visibility = View.GONE
            placementBannerImage.visibility = View.GONE
            yourRankingText.visibility = View.GONE
            leaderboardIcon.visibility = View.GONE
            rankingPlacementText.visibility = View.GONE
        }
        // displays a logout button if not logged in or if staff(staff don't have profile page)



        // Element creation
        // nameText = view.findViewById(R.id.nameText)
        // pointsText = view.findViewById(R.id.ptsText)
        // tierText = view.findViewById(R.id.tierText)
        // qrCodeImage = view.findViewById(R.id.qrCodeImage)
        // waveText = view.findViewById(R.id.waveText)
        //noneText = view.findViewById(R.id.noneText)
        //vegetarianText = view.findViewById(R.id.vegetarianText)
        //veganText = view.findViewById(R.id.veganText)
        //dairyFreeText = view.findViewById(R.id.dairyFreeText)
        //glutenFreeText = view.findViewById(R.id.glutenFreeText)
        //otherText = view.findViewById(R.id.otherText)

        // Logout button
        val logoutButton1 = view.findViewById<ImageButton>(R.id.logoutButton)
        logoutButton1.setOnClickListener {
            val mainActivity: MainActivity = requireActivity() as MainActivity
            mainActivity.logout()
        }

        // flip animation
//        var scale = requireActivity().applicationContext.resources.displayMetrics.density
//        val front = view.findViewById<ConstraintLayout>(R.id.ticket_front)
//        val back = view.findViewById<ImageView>(R.id.ticket_back)
//        front.cameraDistance = 8000 * scale
//        back.cameraDistance = 8000 * scale
//        front_anim =
//            AnimatorInflater.loadAnimator(context, R.animator.front_animator) as AnimatorSet
//        back_anim = AnimatorInflater.loadAnimator(context, R.animator.back_animator) as AnimatorSet

//        val flipButton = view.findViewById<Button>(R.id.flipButton)
//        flipButton.setOnClickListener {
//            flipButton.setClickable(false)
//            if (isFront) {
//                front_anim.setTarget(front)
//                back_anim.setTarget(back)
//                front_anim.start()
//                back_anim.start()
//                isFront = false
//            } else {
//                front_anim.setTarget(back)
//                back_anim.setTarget(front)
//                back_anim.start()
//                front_anim.start()
//                isFront = true
//            }
//            Handler().postDelayed({ flipButton.setClickable(true) }, 1200)
//        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // if user isn't a staff and has logged in (i.e. attendee), show profile
        if (hasLoggedIn()) {
            viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
            viewModel.qr.observe(
                viewLifecycleOwner,
                Observer {
                    updateQrView(it)
                },
            )
            viewModel.currentProfileLiveData.observe(this, Observer { updateProfileUI(it) })
        }
    }

    private fun updateDietaryRestrictions(attendee: Attendee?) = attendee?.let { it ->
//        val dietary = it.dietary
//        if (it.dietary.isEmpty() || (it.dietary.size == 1 && it.dietary[0] == "None")) {
//            noneText.visibility = View.VISIBLE
//        } else {
//            for (diet in dietary) {
//                when (diet) {
//                    "Vegetarian" -> vegetarianText.visibility = View.VISIBLE
//                    "Vegan" -> veganText.visibility = View.VISIBLE
//                    "Lactose Intolerant" -> dairyFreeText.visibility = View.VISIBLE
//                    "Gluten Free" -> glutenFreeText.visibility = View.VISIBLE
//                    else -> otherText.visibility = View.VISIBLE
//                }
//            }
//        }
    }

    private fun updateProfileUI(profile: Profile?) = profile?.let { it ->
        Log.d("Update Profile UI", "Reached profile UI")
        waveText.text = "Wave ${it.foodWave}"
        // need to update this later
        attendeeTypeText.text= "General"
        nameText.text = it.displayName
        ImageLoadingUtilities().fetchSVG(requireContext(), it.avatarUrl, avatarImage)
        avatarImage.setImageResource(R.drawable.avatar_image_svg)




    //        val currPoints = it.points
//        pointsText.text = "$currPoints pts"
//        nameText.text = it.displayName
//        waveText.text = "Wave ${it.foodWave}"
//
//        // TODO: change this so it uses api calls
//        when {
//            currPoints < 900 -> {
//                tierText.text = "Clown Tier"
//            }
//            currPoints < 1200 -> {
//                tierText.text = "Juggler Tier"
//            }
//            else -> {
//                tierText.text = "Acrobat Tier"
//            }
//        }
    }
    private fun updateQrView(qr: QR?) = qr?.let { it ->
        if (qrCodeImage.width > 0 && qrCodeImage.height > 0) {
            // Retrieves qr code user info that will be encoded
            val text = "hackillinois://user?userId=\\(${qr.userId})"
            // Creates bitmap of text
            val bitmap = generateQR(text)
            // actually setting the qr code to be the generated qr code
            qrCodeImage?.setImageBitmap(bitmap)
        }
    }

    private fun generateQR(text: String): Bitmap {
        val width = qrCodeImage.width
        val height = qrCodeImage.height

        // One-dimensional array representing the pixels of the qr code
        val pixels = IntArray(width * height)

        val multiFormatWriter = MultiFormatWriter()
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.MARGIN] = 0

        try {
            val bitMatrix =
                multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints)

            val clear = Color.TRANSPARENT
            val solid = Color.parseColor("#964C1A")
            val black = Color.BLACK
            // creates qr code based on bitMatrix
            for (x in 0 until width) {
                for (y in 0 until (height)) {
                    pixels[y * width + x] = (if (bitMatrix.get(x, y)) solid else clear)
                }
            }
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }

    private fun hasLoggedIn(): Boolean {
        // Reads JWT and checks if it is equal to an empty JWT
        return JWTUtilities.readJWT(requireActivity().applicationContext) != JWTUtilities.DEFAULT_JWT
    }

    private fun isStaff(): Boolean {
        val context = requireActivity().applicationContext
        val prefString = context.getString(R.string.authorization_pref_file_key)
        return context.getSharedPreferences(prefString, Context.MODE_PRIVATE).getString("provider", "") ?: "" == "google"
    }
}
