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


class ProfileEditSkillsFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel

    private lateinit var backButton: ImageButton
    private lateinit var doneText: TextView
    private lateinit var skillsLinearLayout: LinearLayout

    private lateinit var currentProfile: Profile

    private lateinit var skillsArray: Array<String>
    private lateinit var checkBoxMap: HashMap<String, CheckBox>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        viewModel.init()
        viewModel.currentProfileLiveData.observe(this, Observer { updateProfileUI(it) })

        currentProfile = Profile("", "", "", 0, "", "", "", "",
                "", emptyList())
        skillsArray = resources.getStringArray(R.array.skills_array)
        checkBoxMap = HashMap()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile_edit_skills, container, false)

        backButton = view.findViewById(R.id.backButton)
        doneText = view.findViewById(R.id.doneText)
        skillsLinearLayout = view.findViewById(R.id.skillsLinearLayout)

        backButton.setOnClickListener {
            (activity as MainActivity).switchFragment(ProfileEditFragment(), false)
        }

        skillsArray.forEach { it ->
            val checkBox = CheckBox(activity)
            checkBox.layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            checkBox.text = it
            checkBox.setTextColor(0xFFFFFFFF.toInt())
            checkBox.typeface = context?.let { it1 -> ResourcesCompat.getFont(it1, R.font.montserrat_bold) }

            checkBox.setPadding(20, 40, 20, 40)

            val colorStateList = ColorStateList(arrayOf(intArrayOf(-android.R.attr.state_enabled), intArrayOf(android.R.attr.state_enabled)), intArrayOf(
                    Color.BLACK,  //disabled
                    Color.WHITE //enabled
            ))
            checkBox.buttonTintList = colorStateList

            checkBoxMap[it] = checkBox
            skillsLinearLayout.addView(checkBox)
        }

        doneText.setOnClickListener {
            var selectedSkills = mutableListOf<String>()
            skillsArray.forEach {
                if (checkBoxMap[it]?.isChecked!!) {
                    selectedSkills.add(it)
                }
            }
            currentProfile.interests = selectedSkills

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

        currentProfile.interests.forEach {
            checkBoxMap[it]?.isChecked = true
        }
    }
}