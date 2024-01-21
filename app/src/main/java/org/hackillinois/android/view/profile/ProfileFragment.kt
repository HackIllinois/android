package org.hackillinois.android.view.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import org.hackillinois.android.R
import org.hackillinois.android.common.JWTUtilities
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

    private var pro = false
    var staff = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        staff = isStaff()
        pro = isPro()
        if (!hasLoggedIn() or staff) {
            return
        }
        // View Model Set Up
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        viewModel.init()
        viewModel.currentProfileLiveData.observe(this, Observer { updateProfileUI(it) })
        viewModel.qr.observe(this, Observer { updateQrView(it) })
        viewModel.attendee.observe(this) { }



        // view model initialization
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!hasLoggedIn() or (hasLoggedIn() and staff)) {
            val view = inflater.inflate(R.layout.fragment_profile_not_logged_in, container, false)
            val logoutButton = view.findViewById<Button>(R.id.logout_button)
            logoutButton.setOnClickListener {
                val mainActivity: MainActivity = requireActivity() as MainActivity
                mainActivity.logout()
            }
            return view
        }
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
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
        // displays a logout button if not logged in or if staff(staff don't have profile page)
        // Logout button
        val logoutButton1 = view.findViewById<ImageButton>(R.id.logoutButton)
        logoutButton1.setOnClickListener {
            val mainActivity: MainActivity = requireActivity() as MainActivity
            mainActivity.logout()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // if user isn't a staff and has logged in (i.e. attendee), show profile
        if (hasLoggedIn() and !staff) {
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


    private fun updateProfileUI(profile: Profile?) = profile?.let { it ->
        waveText.text = "Wave ${it.foodWave}"
        if (!pro) {
            attendeeTypeText.text = "General" // todo: need to update this later
        } else {
            attendeeTypeText.text = "Knight"
        }
        nameText.text = it.displayName
        // load avatar image png from API using Glide
        Glide.with(requireContext()).load(it.avatarUrl).into(avatarImage)
    }
    private fun updateQrView(qr: QR?) = qr?.let { it ->
        if (qrCodeImage.width > 0 && qrCodeImage.height > 0) {
            // Retrieves qr code user info that will be encoded
            val text = "${qr.qrInfo}"
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

    private fun isPro(): Boolean {

        return false
    }
}
