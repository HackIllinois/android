package org.hackillinois.android.view.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Profile
import java.text.SimpleDateFormat
import java.util.*


class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel

    private lateinit var profileImage: ImageView

    private lateinit var skillsLayout: LinearLayout

    private lateinit var nameText: TextView
    private lateinit var teamStatusText: TextView
    private lateinit var timezoneText: TextView
    private lateinit var timeText: TextView
    private lateinit var pointsText: TextView
    private lateinit var discordUsernameText: TextView
    private lateinit var descriptionText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        viewModel.init()
        viewModel.currentProfileLiveData.observe(this, Observer { updateProfileUI(it) })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        profileImage = view.findViewById(R.id.profileImage)
        nameText = view.findViewById(R.id.nameText)
        teamStatusText = view.findViewById(R.id.teamStatusText)
        timezoneText = view.findViewById(R.id.timezoneText)
        timeText = view.findViewById(R.id.timeText)
        pointsText = view.findViewById(R.id.pointsText)
        discordUsernameText = view.findViewById(R.id.discordUsernameText)
        descriptionText = view.findViewById(R.id.descriptionText)
        skillsLayout = view.findViewById(R.id.skillsLayout)

//        Log.d("TAG", viewModel.currentProfileLiveData.value!!.firstName)
//        Log.d("TAG", "ALL PROFILES" + viewModel.allProfilesLiveData.value!!.get(0).firstName)

        return view
    }

    private fun updateProfileUI(profile: Profile?) = profile?.let { it ->
        timezoneText.text = it.timezone
        discordUsernameText.text = it.discord
        descriptionText.text = it.description
        pointsText.text = it.points.toString()

        nameText.text = it.firstName + " " + it.lastName

        teamStatusText.text = it.teamStatus.replace("_", " ").toLowerCase(Locale.ROOT)

        val df = SimpleDateFormat("hh:mm aa")
        timeText.text = df.format(Calendar.getInstance().time)

        try {
            context?.let { it1 -> Glide.with(it1).load(it.avatarUrl).centerCrop().placeholder(R.drawable.ic_star_border).into(profileImage) }
        } catch (e: Exception) {
            Log.e("Load profile image", e.toString())
        }

        it.interests.forEach {
            val textView = TextView(activity)
            textView.layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            textView.text = it
            textView.setTextColor(0xFFFFFFFF.toInt())
            textView.setBackgroundResource(R.drawable.rounded_salmon_bg)
            textView.setPadding(20, 20, 20, 20) // in pixels (left, top, right, bottom)

            skillsLayout.addView(textView)
        }
    }
}