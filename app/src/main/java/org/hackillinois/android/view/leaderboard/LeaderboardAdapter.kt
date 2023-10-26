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

    // onCreateViewHolder used to display scrollable list of items
    // implemented as part of RecyclerView's adapter, responsible for creating new ViewHolder objects
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutResource = R.layout.leaderboard_tile
        val view = LayoutInflater.from(parent.context).inflate(layoutResource, parent, false)
        val viewHolder = ViewHolder(view)
        context = parent.context
        return viewHolder
    }

    override fun getItemCount() = itemList.size

    // onBindViewHolder called when ViewHolder needs to be filled with data
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        // populating views within ViewHolder with data from 'item'
        // position is zero-indexed but we want the leaderboard to start at 1
        bind(item, holder.itemView, position + 1)
    }

    // bind used to update views within a ViewHolder in a RecyclerView that displays a leaderboard
    private fun bind(item: Leaderboard, itemView: View, position: Int) {
        itemView.apply {
            // sets text of a TextView (rankTextView) to the 'position' value converted to a string
            // used to display the rank of the item in the leaderboard
            rankTextView.text = position.toString()
            // used to display discord username ?
            discordTextView.text = item.displayName
            val pointNum = item.points
            pointsTextView.text = resources.getQuantityString(R.plurals.leaderboard_points_view, pointNum, pointNum)

            if (position == 1) { // first position
                leaderboardCardView.setBackgroundResource(R.drawable.leaderboard_2023_top_bg)
            } else if (position == itemCount) { // last position (itemCount is implicit)
                leaderboardCardView.setBackgroundResource(R.drawable.leaderboard_2023_bottom_bg)
            } else {
                leaderboardCardView.setBackgroundColor(ContextCompat.getColor(context, R.color.cloudMist))
            }
        }
    }

    fun updateLeaderboard(leaderboard: List<Leaderboard>) {
        this.itemList = leaderboard
        notifyDataSetChanged()
    }
}
