package org.hackillinois.android.view.leaderboard

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Leaderboard

class LeaderboardAdapter(private val frag: Fragment, private val resources: Resources) :
    RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    var data = listOf<Leaderboard>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        Log.d("ON BIND VIEW HOLDER", item.toString())
        holder.bind(item, resources)
    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val discordTextView: TextView = itemView.findViewById(R.id.discord_textview)
        private val pointsTextView: TextView = itemView.findViewById(R.id.points_textview)
        private lateinit var context: Context

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.leaderboard_tile, parent, false)
                val viewHolder = ViewHolder(view)
                viewHolder.context = parent.context
                return viewHolder
            }
        }

        fun bind(item: Leaderboard, resources: Resources) {
            discordTextView.text = item.discord
            pointsTextView.text = item.points.toString()
        }
    }
}
