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
import org.hackillinois.android.model.leaderboard.LeaderboardEntity
import org.hackillinois.android.repository.ProfileRepository
import org.hackillinois.android.view.MainActivity
import org.hackillinois.android.viewmodel.LeaderboardViewModel

class LeaderboardFragment : Fragment() {

    companion object {
        fun newInstance() = LeaderboardFragment()
    }

    private lateinit var viewModel: LeaderboardViewModel
    private var allProfiles: List<LeaderboardEntity> = listOf()
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
            val view = inflater.inflate(R.layout.fragment_leaderboard_not_logged_in, container, false)
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

        recyclerView.adapter = leaderboardAdapter

        viewModel.leaderboardLiveData.observe(
            viewLifecycleOwner,
            Observer {
                allProfiles = it as List<LeaderboardEntity>
            }
        )
        return view
    }

    private fun hasLoggedIn(): Boolean {
        return JWTUtilities.readJWT(activity!!.applicationContext) != JWTUtilities.DEFAULT_JWT
    }
}
