package org.hackillinois.android.view.leaderboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_points_shop.view.*
import kotlinx.android.synthetic.main.fragment_points_shop.view.recyclerview_points_shop
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
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_points_shop, container, false)

        mAdapter = LeaderboardAdapter(leaderboard)

        recyclerView = view.recyclerview_points_shop.apply {
            mLayoutManager = LinearLayoutManager(context)
            this.layoutManager = mLayoutManager
            this.adapter = mAdapter
            addDividers()
        }

        viewModel.leaderboardLiveData.observe(
            viewLifecycleOwner,
            Observer {
                updateLeaderboard(it)
            },
        )
        return view
    }

    class DividerItemDecorator(context: Context) : RecyclerView.ItemDecoration() {
        private val mDivider: Drawable = ContextCompat.getDrawable(context, R.drawable.points_shop_divider)!!
        override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            val dividerLeft = parent.paddingLeft
            val dividerRight = parent.width - parent.paddingRight
            val childCount = parent.childCount // how many items recyclerview has
            for (i in 0..childCount - 1) { // minus 2 to account for zero index and skip last item
                val child = parent.getChildAt(i)
                val params = child.layoutParams as RecyclerView.LayoutParams
                val dividerTop = child.bottom + params.bottomMargin
                val dividerBottom = dividerTop + mDivider.intrinsicHeight
                mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
                mDivider.draw(canvas)

                if (i == 0) {
                    val topDividerTop = parent.paddingTop
                    val topDividerBottom = topDividerTop + mDivider.intrinsicHeight
                    mDivider.setBounds(dividerLeft, topDividerTop, dividerRight, topDividerBottom)
                    mDivider.draw(canvas)
                }
            }
        }
    }
    fun RecyclerView.addDividers() {
        if (layoutManager !is LinearLayoutManager) {
            return
        }

        addItemDecoration(DividerItemDecorator(context))
    }

    private fun updateLeaderboard(newLeaderboard: List<Leaderboard>) {
        mAdapter.updateLeaderboard(newLeaderboard)
        Log.d("UPDATE LEADERBOARD", leaderboard.toString())
    }
}

// var itemDecoration = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
// itemDecoration.setDrawable(getDrawable(this.context, R.drawable.leaderboard_divider)!!)
// addItemDecoration(itemDecoration)
