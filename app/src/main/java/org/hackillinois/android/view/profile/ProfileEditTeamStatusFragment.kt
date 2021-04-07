package org.hackillinois.android.view.profile

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.view.MainActivity


class ProfileEditTeamStatusFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel

    private lateinit var backButton: ImageButton
    private lateinit var doneText: TextView
    private lateinit var radioGroup: RadioGroup

    private lateinit var currentProfile: Profile

    private lateinit var teamStatusArray: Array<String>
    private lateinit var teamStatusVerboseArray: Array<String>
    private lateinit var radioButtonMap: HashMap<String, RadioButton>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        viewModel.init()
        viewModel.currentProfileLiveData.observe(this, Observer { updateProfileUI(it) })

        currentProfile = Profile("", "", "", 0, "", "", "", "",
                "", emptyList())
        teamStatusArray = resources.getStringArray(R.array.team_status_array)
        teamStatusVerboseArray = resources.getStringArray(R.array.team_status_verbose_array)
        radioButtonMap = HashMap()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile_edit_team_status, container, false)

        backButton = view.findViewById(R.id.backButton)
        doneText = view.findViewById(R.id.doneText)
        radioGroup = view.findViewById(R.id.radioGroup)

        backButton.setOnClickListener {
            (activity as MainActivity).switchFragment(ProfileEditFragment(), false)
        }

        teamStatusArray.forEachIndexed { index, it ->
            val radioButton = RadioButton(activity)
            radioButton.layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            radioButton.text = teamStatusVerboseArray[index]
            radioButton.setTextColor(0xFFFFFFFF.toInt())
            radioButton.typeface = context?.let { it1 -> ResourcesCompat.getFont(it1, R.font.montserrat_bold) }

            radioButton.setPadding(20, 40, 20, 40)

            val colorStateList = ColorStateList(arrayOf(intArrayOf(-android.R.attr.state_enabled), intArrayOf(android.R.attr.state_enabled)), intArrayOf(
                    Color.BLACK,  //disabled
                    Color.WHITE //enabled
            ))
            radioButton.buttonTintList = colorStateList

            radioButtonMap[it] = radioButton
            radioGroup.addView(radioButton)
        }

        doneText.setOnClickListener {
            var selectedTeamStatus = ""
            teamStatusArray.forEach {
                if (radioButtonMap[it]?.isChecked!!) {
                    selectedTeamStatus = it
                }
            }
            currentProfile.teamStatus = selectedTeamStatus

            viewModel.updateProfile(currentProfile)
            (activity as MainActivity).switchFragment(ProfileEditFragment(), false)
        }

        return view
    }

    private fun updateProfileUI(profile: Profile?) = profile?.let { it ->
        currentProfile.id = it.id
        currentProfile.firstName = it.firstName
        currentProfile.lastName = it.lastName
        currentProfile.points = it.points
        currentProfile.timezone = it.timezone
        currentProfile.avatarUrl = it.avatarUrl
        currentProfile.discord = it.discord
        currentProfile.teamStatus = it.teamStatus
        currentProfile.description = it.description
        currentProfile.interests = it.interests

        radioButtonMap[currentProfile.teamStatus]?.isChecked = true
    }
}