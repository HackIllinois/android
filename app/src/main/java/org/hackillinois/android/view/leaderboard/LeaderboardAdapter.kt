package org.hackillinois.android.view.leaderboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.leaderboard_tile.view.*
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Leaderboard

class LeaderboardAdapter(private var itemList: List<Leaderboard>) :
    RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {
    private lateinit var context: Context

    inner class ViewHolder(parent: View) : RecyclerView.ViewHolder(parent)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        Log.d("Creating recyclerview", "")
        val layoutResource = R.layout.leaderboard_tile
        val view = LayoutInflater.from(parent.context).inflate(layoutResource, parent, false)
        val viewHolder = ViewHolder(view)
        context = parent.context
        return viewHolder
    }

    override fun getItemCount() = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        // position is zero-indexed but we want the leaderboard to start at 1
        bind(item, holder.itemView, position + 1)
    }

    private fun bind(item: Leaderboard, itemView: View, position: Int) {
        itemView.apply {
            rankTextView.text = position.toString()
            discordTextView.text = item.discord
            pointsTextView.text = item.points.toString()

            if (position == 1) {
                leaderboardCardView.setBackgroundResource(R.drawable.leaderboard_top_bg)
            } else if (position == 10) {
                leaderboardCardView.setBackgroundResource(R.drawable.leaderboard_top_bg)
            }

            if (position % 2 == 1) {
                leaderboardCardView.setBackgroundColor(ContextCompat.getColor(context, R.color.leaderboardLight))
            } else {
                leaderboardCardView.setBackgroundColor(ContextCompat.getColor(context, R.color.leaderboardDark))
            }
        }
    }

    fun updateLeaderboard(leaderboard: List<Leaderboard>) {
        this.itemList = leaderboard
        notifyDataSetChanged()
    }
}
