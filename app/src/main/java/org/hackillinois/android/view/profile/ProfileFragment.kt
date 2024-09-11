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
import kotlinx.android.synthetic.main.fragment_point_shop.coin_total_textview
import org.hackillinois.android.R
import org.hackillinois.android.common.JWTUtilities
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.database.entity.QR
import org.hackillinois.android.database.entity.Roles
import org.hackillinois.android.model.profile.Ranking
import org.hackillinois.android.view.MainActivity
import org.hackillinois.android.viewmodel.ProfileViewModel
import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var nameText: TextView

    private lateinit var qrCodeImage: ImageView
    private lateinit var avatarImage: ImageView
    private lateinit var waveText: TextView
    private lateinit var attendeeTypeText: TextView
    private lateinit var rankingPlacementText: TextView

    private var pro = false
    private var userRoles: Roles? = null

    override fun onPause() {
        super.onPause()
        if (isAttendee()) {
            profileViewModel.stopTimer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isAttendee()) {
            profileViewModel.startTimer()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasLoggedIn()) {
            return
        }
        // View model set up and initialization
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        profileViewModel.init()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // if not an attendee, set the layout to be the not logged in profile
        if (!hasLoggedIn()) {
            val view = inflater.inflate(R.layout.fragment_profile_not_logged_in, container, false)
            val logoutButton = view.findViewById<Button>(R.id.logout_button)
            logoutButton.setOnClickListener {
                val mainActivity: MainActivity = requireActivity() as MainActivity
                mainActivity.logout()
            }
            return view
        }

        // set up LiveData observers
        if (true) {
            profileViewModel.currentProfileLiveData.observe(
                this@ProfileFragment,
                Observer {
                    updateProfileUI(it)
                    updateStaffPoints(it)
                }
            )
        } else {
            profileViewModel.currentProfileLiveData.observe(
                this@ProfileFragment,
                Observer {
                    updateProfileUI(it)
//                updateStaffPoints(it)
                }
            )
        }
        profileViewModel.qr.observe(
            this@ProfileFragment,
            Observer {
                updateQrView(it)
            }
        )
        profileViewModel.roles.observe(
            this@ProfileFragment,
            Observer {
                userRoles = it
                if (userRoles != null && userRoles!!.isPro() && false) {
                    pro = it.isPro()
                    updateProTag()
                }
            }
        )

        profileViewModel.ranking.observe(
            this@ProfileFragment,
            Observer {
//                updateRanking(it)
            }
        )

        // do view creation here if attendee

        if (true) {
            val view = inflater.inflate(R.layout.fragment_profile_staff, container, false)
            nameText = view.findViewById(R.id.nameText)
            qrCodeImage = view.findViewById(R.id.qrCodeImage2024)
            avatarImage = view.findViewById(R.id.avatarImage)
            waveText = view.findViewById(R.id.waveText)
            attendeeTypeText = view.findViewById(R.id.attendeeTypeText)
            rankingPlacementText = view.findViewById(R.id.rankingPlacementTextView)

            val logoutButton = view.findViewById<ImageButton>(R.id.logoutButton)
            logoutButton.setOnClickListener {
                val mainActivity: MainActivity = requireActivity() as MainActivity
                mainActivity.logout()
            }

            return view

        } else { // attendee page
            val view = inflater.inflate(R.layout.fragment_profile, container, false)
            nameText = view.findViewById(R.id.nameText)
            qrCodeImage = view.findViewById(R.id.qrCodeImage2024)
            avatarImage = view.findViewById(R.id.avatarImage)
            waveText = view.findViewById(R.id.waveText)
            attendeeTypeText = view.findViewById(R.id.attendeeTypeText)
            rankingPlacementText = view.findViewById(R.id.rankingPlacementTextView)

            val logoutButton = view.findViewById<ImageButton>(R.id.logoutButton)
            logoutButton.setOnClickListener {
                val mainActivity: MainActivity = requireActivity() as MainActivity
                mainActivity.logout()
            }

            return view
        }


        // Displays the logout button in the top-right corner if an attendee


//        return view
    }

    private fun updateProfileUI(profile: Profile?) = profile?.let { it ->
        waveText.text = "Wave ${it.foodWave}"
        nameText.text = it.displayName
        if (true) {
            waveText.text = "";
            attendeeTypeText.text = "Staff"
        }
        if (true) {

        } else {
            updateProTag()

        }

        // load avatar image png from API using Glide
        Glide.with(requireContext()).load(it.avatarUrl).into(avatarImage)
    }

    private fun updateRanking(ranking: Ranking?) = ranking?.let { it ->
        rankingPlacementText.text = "${ranking.ranking}"
    }

    private fun updateStaffPoints(newProfile: Profile) {
        if (newProfile != null) {
            rankingPlacementText.text = String.format("%,d", newProfile.points)
        }
    }

    private fun updateProTag() {
        attendeeTypeText.text = if (pro) "Knight" else "General"
    }
    private fun updateQrView(qr: QR?) = qr?.let { it ->
        if (qrCodeImage.width > 0 && qrCodeImage.height > 0) {
            // Retrieves qr code user info that will be encoded
            val text = qr.qrInfo
            // Creates bitmap of text
            val bitmap = generateQR(text)
            // actually setting the qr code to be the generated qr code
            qrCodeImage.setImageBitmap(bitmap)
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
            val solid = Color.parseColor("#662B13")
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

    private fun isAttendee(): Boolean {
        val context = requireActivity().applicationContext
        val prefString = context.getString(R.string.authorization_pref_file_key)
        return context.getSharedPreferences(prefString, Context.MODE_PRIVATE).getString("provider", "") ?: "" == "github"
    }
}
