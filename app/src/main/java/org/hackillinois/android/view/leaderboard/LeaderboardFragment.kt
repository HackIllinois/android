package org.hackillinois.android.view.leaderboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_leaderboard.view.*
import kotlinx.android.synthetic.main.fragment_schedule_day.view.*
import kotlinx.android.synthetic.main.fragment_schedule_day.view.activity_schedule_recyclerview
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Leaderboard
import org.hackillinois.android.viewmodel.LeaderboardViewModel

class LeaderboardFragment : Fragment() {

    companion object {
        fun newInstance() = LeaderboardFragment()
    }

    private lateinit var viewModel: LeaderboardViewModel
    private var leaderboard: List<Leaderboard> = listOf()
    private lateinit var recyclerView: RecyclerView
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAdapter: LeaderboardAdapter

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

        mAdapter = LeaderboardAdapter(leaderboard)


        recyclerView = view.recyclerview_leaderboard.apply {
            mLayoutManager = LinearLayoutManager(context)
            this.layoutManager = mLayoutManager
            this.adapter = mAdapter
        }

        viewModel.leaderboardLiveData.observe(
            viewLifecycleOwner,
            Observer {
                updateLeaderboard(it)
            }
        )
        return view
    }

    private fun updateLeaderboard(newLeaderboard: List<Leaderboard>) {
        mAdapter.updateLeaderboard(newLeaderboard)
        Log.d("UPDATE LEADERBOARD", leaderboard.toString())
    }
}
