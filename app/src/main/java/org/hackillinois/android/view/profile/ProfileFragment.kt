package org.hackillinois.android.view.profile

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import org.hackillinois.android.R
import org.hackillinois.android.common.JWTUtilities
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.view.MainActivity
import org.hackillinois.android.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var ticketImage: ImageView
    private lateinit var nameText: TextView
    private lateinit var pointsText: TextView
    private lateinit var discordText: TextView
    private lateinit var tierText: TextView

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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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

        ticketImage = view.findViewById(R.id.ticket_front)
        nameText = view.findViewById(R.id.nameText)

        pointsText = view.findViewById(R.id.ptsText)
        discordText = view.findViewById(R.id.ptsText)
        tierText = view.findViewById(R.id.tierText)



        val logoutButton1 = view.findViewById<ImageButton>(R.id.logoutButton)
        logoutButton1.setOnClickListener {
            val mainActivity: MainActivity = requireActivity() as MainActivity
            mainActivity.logout()
        }

        var scale = requireActivity().applicationContext.resources.displayMetrics.density
        val front = view.findViewById<ImageView>(R.id.ticket_front)
        val back = view.findViewById<ImageView>(R.id.ticket_back)
        val flip = view.findViewById<Button>(R.id.flipButton)
        front.cameraDistance = 8000 * scale
        back.cameraDistance = 8000 * scale
        front_anim = AnimatorInflater.loadAnimator(context, R.animator.front_animator) as AnimatorSet
        back_anim = AnimatorInflater.loadAnimator(context, R.animator.back_animator) as AnimatorSet

        flip.setOnClickListener{
            if(isFront) {
                front_anim.setTarget(front);
                back_anim.setTarget(back);
                front_anim.start()
                back_anim.start()
                isFront = false
            }
            else {
                front_anim.setTarget(back)
                back_anim.setTarget(front)
                back_anim.start()
                front_anim.start()
                isFront = true
            }
        }
        return view
    }

    private fun updateProfileUI(profile: Profile?) = profile?.let { it ->
        val currPoints = it.points
        discordText.text = it.discord
        pointsText.text = "${currPoints.toString()} pts"
        nameText.text = "${it.firstName} ${it.lastName}"

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
        when {
            currPoints < 500 -> {
                tierText.text = "Tier: Flour"
            }
            currPoints < 800 -> {
                tierText.text = "Tier: Cookie"
            }
            else -> {
                tierText.text = "Tier: Cake"
            }
        }
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
