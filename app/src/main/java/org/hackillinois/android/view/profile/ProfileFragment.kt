package org.hackillinois.android.view.profile

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.view.MainActivity
import java.text.SimpleDateFormat
import java.util.*


class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel

    private lateinit var profileImage: ImageView

    private lateinit var skillsLayout: LinearLayout

    private lateinit var editButton: ImageButton

    private lateinit var nameText: TextView
    private lateinit var teamStatusText: TextView
    private lateinit var timezoneText: TextView
    private lateinit var timeText: TextView
    private lateinit var pointsText: TextView
    private lateinit var discordText: TextView
    private lateinit var descriptionText: TextView

    private lateinit var teamStatusArray: Array<String>
    private lateinit var teamStatusVerboseArray: Array<String>
    private lateinit var teamStatusColors: Array<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        viewModel.init()
        viewModel.currentProfileLiveData.observe(this, Observer { updateProfileUI(it) })

        teamStatusArray = resources.getStringArray(R.array.team_status_array)
        teamStatusVerboseArray = resources.getStringArray(R.array.team_status_verbose_array)
        teamStatusColors = arrayOf(R.color.lookingForTeamColor, R.color.lookingForMembersColor, R.color.notLookingColor)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        profileImage = view.findViewById(R.id.profileImage)
        nameText = view.findViewById(R.id.nameText)
        teamStatusText = view.findViewById(R.id.teamStatusText)
        timezoneText = view.findViewById(R.id.timezoneText)
        timeText = view.findViewById(R.id.timeText)
        pointsText = view.findViewById(R.id.pointsText)
        discordText = view.findViewById(R.id.discordText)
        descriptionText = view.findViewById(R.id.descriptionText)
        skillsLayout = view.findViewById(R.id.skillsLinearLayout)

        editButton = view.findViewById(R.id.editButton)
        editButton.setOnClickListener {
            Log.d("TAG", "click")
            (activity as MainActivity).switchFragment(ProfileEditFragment(), false)
        }

//        Log.d("TAG", viewModel.currentProfileLiveData.value!!.firstName)
//        Log.d("TAG", "ALL PROFILES" + viewModel.allProfilesLiveData.value!!.get(0).firstName)

        return view
    }

    private fun updateProfileUI(profile: Profile?) = profile?.let { it ->
        Log.d("TAG", "PROFILE UPDATE UI")

        timezoneText.text = it.timezone
        discordText.text = it.discord
        descriptionText.text = it.description
        pointsText.text = it.points.toString()

        nameText.text = it.firstName + " " + it.lastName

        teamStatusArray.forEachIndexed { index, s ->
            if (s == it.teamStatus) {
                teamStatusText.text = "⬤ " + teamStatusVerboseArray[index]
                teamStatusText.setTextColor(resources.getColor(teamStatusColors[index]))
            }
        }

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
            textView.setPadding(20, 20, 20, 20)
            textView.typeface = context?.let { it1 -> ResourcesCompat.getFont(it1, R.font.montserrat_bold) }

            skillsLayout.addView(textView)
        }
    }
}