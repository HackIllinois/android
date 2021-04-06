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
import kotlinx.android.synthetic.main.fragment_event_info.view.*
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.view.MainActivity
import java.text.SimpleDateFormat
import java.util.*


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        viewModel.init()
        viewModel.currentProfileLiveData.observe(this, Observer { updateProfileUI(it) })
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
        firstNameText.setOnClickListener {
            (activity as MainActivity).switchFragment(ProfileEditFirstNameFragment(), false)
        }
        descriptionText.setOnClickListener {
            (activity as MainActivity).switchFragment(ProfileEditDescriptionFragment(), false)
        }
        firstNameText.setOnClickListener {
            (activity as MainActivity).switchFragment(ProfileEditFirstNameFragment(), false)
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
        Log.d("first name", "PROFILE EDIT UPDATE UI")

        firstNameText.text = it.firstName
        lastNameText.text = it.lastName
        descriptionText.text = it.description
        discordText.text = it.discord

        teamStatusText.text = it.teamStatus.replace("_", " ").toLowerCase(Locale.ROOT)

        skillsText.text = it.interests.toString()

        try {
            context?.let { it1 -> Glide.with(it1).load(it.avatarUrl).centerCrop().placeholder(R.drawable.ic_star_border).into(profileImage) }
        } catch (e: Exception) {
            Log.e("Load profile image", e.toString())
        }
    }
}