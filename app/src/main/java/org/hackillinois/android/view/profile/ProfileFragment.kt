package org.hackillinois.android.view.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.hackillinois.android.R
import org.hackillinois.android.common.JWTUtilities
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.view.MainActivity
import org.hackillinois.android.viewmodel.ProfileViewModel
import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var profileImage: ImageView
    private lateinit var nameText: TextView
    private lateinit var pointsText: TextView
    private lateinit var discordText: TextView
    private lateinit var tierText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasLoggedIn()) {
            return
        }
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        viewModel.init()
        viewModel.currentProfileLiveData.observe(this, Observer { updateProfileUI(it) })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!hasLoggedIn()) {
            val view = inflater.inflate(R.layout.fragment_profile_not_logged_in, container, false)
            val logoutButton = view.findViewById<Button>(R.id.logout_button)
            logoutButton.setOnClickListener {
                val mainActivity: MainActivity = requireActivity() as MainActivity
                mainActivity.logout()
            }
            return view
        }

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        profileImage = view.findViewById(R.id.profileImage)
        nameText = view.findViewById(R.id.nameText)

        pointsText = view.findViewById(R.id.pointsText)
        discordText = view.findViewById(R.id.discordText)
        tierText = view.findViewById(R.id.tierText)

        val logoutButton1 = view.findViewById<ImageButton>(R.id.logoutButton)
        logoutButton1.setOnClickListener {
            val mainActivity: MainActivity = requireActivity() as MainActivity
            mainActivity.logout()
        }

        return view
    }

    private fun updateProfileUI(profile: Profile?) = profile?.let { it ->
        val currPoints = it.points
        discordText.text = it.discord
        pointsText.text = currPoints.toString() + "pts"
        nameText.text = it.firstName + " " + it.lastName

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
         * For the sake of time, I didn't bother storing this in a local DB.
         */

        if (currPoints < 500) {
            tierText.text = "Tier: Flour"
        } else if (currPoints < 800) {
        } else {
        }
    }

    private fun hasLoggedIn(): Boolean {
        return JWTUtilities.readJWT(activity!!.applicationContext) != JWTUtilities.DEFAULT_JWT
    }
}
