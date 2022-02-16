package org.hackillinois.android.view.leaderboard

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.leaderboard_tile.view.*
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Leaderboard

class LeaderboardAdapter(private var itemList: List<Leaderboard>) :
    RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {
    private lateinit var context: Context

    inner class ViewHolder(parent: View) : RecyclerView.ViewHolder(parent)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutResource = R.layout.leaderboard_tile
        val view = LayoutInflater.from(parent.context).inflate(layoutResource, parent, false)
        val viewHolder = ViewHolder(view)
        context = parent.context
        return viewHolder
    }

    override fun getItemCount() = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        Log.d("ON BIND VIEW HOLDER", item.toString())
        bind(item, holder.itemView)
    }

    private fun bind(item: Leaderboard, itemView: View) {
        Log.d("Binding item:", item.toString())
        itemView.apply {
            discordTextView.text = item.discord
            pointsTextView.text = item.points.toString()
        }

    }


//    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val discordTextView: TextView = itemView.findViewById(R.id.discord_textview)
//        private val pointsTextView: TextView = itemView.findViewById(R.id.points_textview)
//        private lateinit var context: Context
//
//        companion object {
//            fun from(parent: ViewGroup): ViewHolder {
//                val layoutInflater = LayoutInflater.from(parent.context)
//                val view = layoutInflater.inflate(R.layout.leaderboard_tile, parent, false)
//                val viewHolder = ViewHolder(view)
//                viewHolder.context = parent.context
//                return viewHolder
//            }
//        }
//

//    }
}
