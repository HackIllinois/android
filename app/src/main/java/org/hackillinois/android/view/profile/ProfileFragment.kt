package org.hackillinois.android.view.profile

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import org.hackillinois.android.R
import org.hackillinois.android.common.JWTUtilities
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.view.MainActivity
import org.hackillinois.android.viewmodel.ProfileViewModel
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

        if (!hasLoggedIn()) {
            return
        }
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        viewModel.init()
        viewModel.currentProfileLiveData.observe(this, Observer { updateProfileUI(it) })

        teamStatusArray = resources.getStringArray(R.array.team_status_array)
        teamStatusVerboseArray = resources.getStringArray(R.array.team_status_verbose_array)
        teamStatusColors = arrayOf(R.color.lookingForTeamColor, R.color.lookingForMembersColor, R.color.notLookingColor)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!hasLoggedIn()) {
            val view = inflater.inflate(R.layout.fragment_groupmatching_not_logged_in, container, false)
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
        teamStatusText = view.findViewById(R.id.teamStatusText)
        timezoneText = view.findViewById(R.id.timezoneText)
        timeText = view.findViewById(R.id.timeText)
        pointsText = view.findViewById(R.id.pointsText)
        discordText = view.findViewById(R.id.discordText)
        descriptionText = view.findViewById(R.id.descriptionText)
        skillsLayout = view.findViewById(R.id.skillsLinearLayout)

        editButton = view.findViewById(R.id.editButton)
        editButton.setOnClickListener {
            (activity as MainActivity).switchFragment(ProfileEditFragment(), false)
        }

        val logoutButton1 = view.findViewById<ImageButton>(R.id.logoutButton)
        logoutButton1.setOnClickListener {
            val mainActivity: MainActivity = requireActivity() as MainActivity
            mainActivity.logout()
        }

        return view
    }

    private fun updateProfileUI(profile: Profile?) = profile?.let { it ->
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

        // val df = SimpleDateFormat("hh:mm aa")
        // timeText.text = df.format(Calendar.getInstance().time)
        // just display timezone so it looks consistent with iOS
        timezoneText.text = "time zone"
        timeText.text = it.timezone

        try {
            context?.let { it1 -> Glide.with(it1)
                    .load(it.avatarUrl)
                    .apply(RequestOptions().transforms(CenterCrop(), RoundedCorners(16)))
                    .into(profileImage) }
        } catch (e: Exception) {
            Log.e("Load profile image", e.toString())
        }

        val maxWidth: Int = skillsLayout.measuredWidth - 100 - 48

        var params: LinearLayout.LayoutParams
        var newLL = LinearLayout(context)
        newLL.orientation = LinearLayout.HORIZONTAL
        newLL.gravity = Gravity.START
        newLL.layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        var widthSoFar = 0

        it.interests.forEach {
            val LL = LinearLayout(context)
            LL.orientation = LinearLayout.HORIZONTAL
            LL.gravity = Gravity.START
            LL.layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            val textView = TextView(activity)
            textView.layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            textView.text = it
            textView.setTextColor(0xFFFFFFFF.toInt())
            textView.setBackgroundResource(R.drawable.rounded_salmon_bg)
            textView.setPadding(20, 20, 20, 20)
            textView.typeface = context?.let { it1 -> ResourcesCompat.getFont(it1, R.font.montserrat_bold) }

            textView.measure(0, 0)
            params = LinearLayout.LayoutParams(textView.measuredWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
            params.setMargins(20, 20, 20, 20)

            LL.addView(textView, params)
            LL.measure(0, 0)

            widthSoFar += textView.measuredWidth
            if (widthSoFar >= maxWidth) {
                skillsLayout.addView(newLL)

                newLL = LinearLayout(context)
                newLL.orientation = LinearLayout.HORIZONTAL
                newLL.gravity = Gravity.START
                newLL.layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

                params = LinearLayout.LayoutParams(LL.measuredWidth, LL.measuredHeight)
                newLL.addView(LL, params)
                widthSoFar = LL.measuredWidth
            } else {
                newLL.addView(LL)
            }
        }
        skillsLayout.addView(newLL)
    }

    private fun hasLoggedIn(): Boolean {
        return JWTUtilities.readJWT(activity!!.applicationContext) != JWTUtilities.DEFAULT_JWT
    }
}