package org.hackillinois.android.view.profile

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
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.view.MainActivity


class ProfileEditFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel

    private lateinit var profileImage: ImageView

    private lateinit var backButton: ImageButton

    private lateinit var firstNameText: TextView
    private lateinit var lastNameText: TextView
    private lateinit var teamStatusText: TextView
    private lateinit var descriptionText: TextView
    private lateinit var discordText: TextView
    private lateinit var skillsText: TextView

    private lateinit var teamStatusArray: Array<String>
    private lateinit var teamStatusVerboseArray: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        viewModel.init()
        viewModel.currentProfileLiveData.observe(this, Observer { updateProfileUI(it) })

        teamStatusArray = resources.getStringArray(R.array.team_status_array)
        teamStatusVerboseArray = resources.getStringArray(R.array.team_status_verbose_array)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile_edit, container, false)

        profileImage = view.findViewById(R.id.profileImage)

        backButton = view.findViewById(R.id.backButton)

        firstNameText = view.findViewById(R.id.firstNameText)
        lastNameText = view.findViewById(R.id.lastNameText)
        teamStatusText = view.findViewById(R.id.teamStatusText)
        descriptionText = view.findViewById(R.id.descriptionText)
        discordText = view.findViewById(R.id.discordText)
        skillsText = view.findViewById(R.id.skillsText)

        backButton.setOnClickListener {
            (activity as MainActivity).switchFragment(ProfileFragment(), false)
        }

        firstNameText.setOnClickListener {
            (activity as MainActivity).switchFragment(ProfileEditFirstNameFragment(), false)
        }
        lastNameText.setOnClickListener {
            (activity as MainActivity).switchFragment(ProfileEditLastNameFragment(), false)
        }
        teamStatusText.setOnClickListener {
            (activity as MainActivity).switchFragment(ProfileEditTeamStatusFragment(), false)
        }
        descriptionText.setOnClickListener {
            (activity as MainActivity).switchFragment(ProfileEditDescriptionFragment(), false)
        }
        discordText.setOnClickListener {
            (activity as MainActivity).switchFragment(ProfileEditDiscordFragment(), false)
        }
        skillsText.setOnClickListener {
            (activity as MainActivity).switchFragment(ProfileEditSkillsFragment(), false)
        }

        return view
    }

    private fun updateProfileUI(profile: Profile?) = profile?.let { it ->
        firstNameText.text = truncate(it.firstName)
        lastNameText.text = truncate(it.lastName)
        descriptionText.text = truncate(it.description)
        discordText.text = truncate(it.discord)

        teamStatusArray.forEachIndexed { index, s ->
            if (s == it.teamStatus) {
                teamStatusText.text = truncate(teamStatusVerboseArray[index])
            }
        }

        skillsText.text = truncate(it.interests.toString())

        try {
            context?.let { it1 -> Glide.with(it1)
                    .load(it.avatarUrl)
                    .apply(RequestOptions().transforms(CenterCrop(), RoundedCorners(16)))
                    .placeholder(R.drawable.ic_star_border)
                    .into(profileImage)
            }
        } catch (e: Exception) {
            Log.e("Load profile image", e.toString())
        }
    }

    private fun truncate(str: String): String {
        if (str.length > 20) {
            return str.substring(0, 17) + "..."
        }
        return str
    }
}