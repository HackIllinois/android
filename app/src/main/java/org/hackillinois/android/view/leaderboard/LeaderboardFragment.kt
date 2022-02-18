package org.hackillinois.android.view.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import org.hackillinois.android.R
import org.hackillinois.android.common.JWTUtilities
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.repository.ProfileRepository
import org.hackillinois.android.view.MainActivity
import org.hackillinois.android.viewmodel.LeaderboardViewModel

class LeaderboardFragment : Fragment() {

    companion object {
        fun newInstance() = LeaderboardFragment()
    }

    private lateinit var viewModel: LeaderboardViewModel
    private var allProfiles: List<Profile> = listOf()
    private lateinit var favButton: ImageButton
    private var favFlag = false
    private lateinit var leaderboardAdapter: LeaderboardAdapter
    private lateinit var currentUser: LiveData<Profile>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!hasLoggedIn()) {
            return
        }
        viewModel = ViewModelProvider(this).get(LeaderboardViewModel::class.java)
        viewModel.init()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!hasLoggedIn()) {
            val view = inflater.inflate(R.layout.fragment_groupmatching_not_logged_in, container, false)
            val logoutButton = view.findViewById<Button>(R.id.logout_button)
            logoutButton.setOnClickListener {
                val mainActivity: MainActivity = requireActivity() as MainActivity
                mainActivity.logout()
            }
            return view
        }
        val view = inflater.inflate(R.layout.fragment_leaderboard, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.team_matching_recyclerview_leaderboard)

        currentUser = ProfileRepository.instance.fetchCurrentProfile()
        leaderboardAdapter = LeaderboardAdapter(currentUser, this, resources)
//        currentUser.observe(viewLifecycleOwner, Observer {
//            filterProfiles()
//        })

        recyclerView.adapter = leaderboardAdapter

//        favButton = view.findViewById<ImageButton>(R.id.star_button_leaderboard)
//        favButton.isSelected = favFlag
//        favButton.setOnClickListener {
//            favFlag = !favFlag
//            favButton.isSelected = favFlag
//            filterProfiles()
//        }

        viewModel.allProfilesLiveData.observe(viewLifecycleOwner, Observer {
            allProfiles = it
//            filterProfiles()
        })
        return view
    }

    private fun hasLoggedIn(): Boolean {
        return JWTUtilities.readJWT(activity!!.applicationContext) != JWTUtilities.DEFAULT_JWT
    }

//    private fun filterProfiles() {
//        skillsChecked.value!!.contains(true)
//        if (lookingForTeamFlag && lookingForMemberFlag) {
//            groupAdapter.data = allProfiles.filter { profile -> profile.hasRequiredSkill(skills, skillsChecked.value!!) }
//        } else if (lookingForTeamFlag) {
//            groupAdapter.data = allProfiles.filter { profile -> profile.teamStatus.equals("LOOKING_FOR_TEAM") &&
//                    profile.hasRequiredSkill(skills, skillsChecked.value!!) }
//        } else if (lookingForMemberFlag) {
//            groupAdapter.data = allProfiles.filter { profile -> profile.teamStatus.equals("LOOKING_FOR_MEMBERS") &&
//                    profile.hasRequiredSkill(skills, skillsChecked.value!!) }
//        } else {
//            groupAdapter.data = allProfiles.filter { profile -> profile.hasRequiredSkill(skills, skillsChecked.value!!) }
//        }
//        if (favButton.isSelected) {
//            groupAdapter.data = groupAdapter.data.filter { profile -> FavoritesManager.isFavoritedProfile(requireContext(), profile) }
//        }
//    }
}

// fun Profile.hasRequiredSkill(skills: Array<String>, skillsChecked: BooleanArray): Boolean {
//    if (!skillsChecked.contains(true)) {
//        return true
//    }
//
//    for (i in skills.indices) {
//        if (skillsChecked[i] && this.interests.contains(skills[i])) {
//            Log.i("GroupMatching", skills[i])
//            return true
//        }
//    }
//    return false
// }