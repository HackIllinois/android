package org.hackillinois.android.view.groupmatching

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.model.Group

class GroupAdapter : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

    var data = listOf<Profile>()
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
        holder.bind(item)
    }


    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatarIcon : ImageView = itemView.findViewById(R.id.avatar_icon)
        val nameTextView : TextView = itemView.findViewById(R.id.name_textview)
        val statusTextView : TextView = itemView.findViewById(R.id.status_textview)
        val starButton : ImageButton = itemView.findViewById(R.id.star_button)
        val profileMatch : TextView = itemView.findViewById(R.id.profile_match)
        val descriptionTextView: TextView = itemView.findViewById(R.id.description_textview)

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.group_matching_tile, parent, false)
                return ViewHolder(view)
            }
        }

        fun bind(item: Profile) {
            nameTextView.text = item.firstName + " " + item.lastName
            statusTextView.text = item.teamStatus
            // TODO: what to put here? discord username?
            profileMatch.text = item.discord
            descriptionTextView.text = item.description
        }
    }
}