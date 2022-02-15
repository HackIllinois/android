package org.hackillinois.android.view.leaderboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Leaderboard
import org.hackillinois.android.viewmodel.LeaderboardViewModel

class LeaderboardFragment : Fragment() {

    companion object {
        fun newInstance() = LeaderboardFragment()
    }

    private lateinit var viewModel: LeaderboardViewModel
    private var allProfiles: List<Leaderboard> = listOf()
    private lateinit var leaderboardAdapter: LeaderboardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LeaderboardViewModel::class.java)
        viewModel.init()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_leaderboard, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerview_leaderboard)
        leaderboardAdapter = LeaderboardAdapter(this, resources)

        recyclerView.adapter = leaderboardAdapter

        viewModel.leaderboardLiveData.observe(
            viewLifecycleOwner,
            Observer {
                allProfiles = it
            }
        )
        Log.d("ALL PROFILES", allProfiles.toString())
        return view
    }
}
