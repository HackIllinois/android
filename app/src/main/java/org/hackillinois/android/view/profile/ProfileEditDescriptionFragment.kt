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
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.view.MainActivity
import org.hackillinois.android.viewmodel.ProfileViewModel

class ProfileEditDescriptionFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel

    private lateinit var backButton: ImageButton
    private lateinit var doneText: TextView
    private lateinit var editText: EditText

    private lateinit var currentProfile: Profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        viewModel.init()
        viewModel.currentProfileLiveData.observe(this, Observer { updateProfileUI(it) })

        currentProfile = Profile("", "", "", 0, "", "", "", "",
                "", emptyList())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile_edit_description, container, false)

        backButton = view.findViewById(R.id.backButton)
        doneText = view.findViewById(R.id.doneText)
        editText = view.findViewById(R.id.editText)

        backButton.setOnClickListener {
            (activity as MainActivity).switchFragment(ProfileEditFragment(), false)
        }

        doneText.setOnClickListener {
            Log.d("TAG", "current profile: " + currentProfile.toString())
            if (editText.text.toString().isNotEmpty()) {
                currentProfile.description = editText.text.toString()
                viewModel.updateProfile(currentProfile)
            }
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
    }
}