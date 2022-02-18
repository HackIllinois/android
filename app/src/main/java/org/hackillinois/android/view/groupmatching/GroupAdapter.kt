package org.hackillinois.android.view.groupmatching

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import org.hackillinois.android.R
import org.hackillinois.android.common.FavoritesManager
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.view.MainActivity
import org.hackillinois.android.view.profile.ProfileFragment
import java.lang.Exception

class GroupAdapter(private val currProfile: LiveData<Profile>, private val frag: Fragment, private val resources: Resources) :
    RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

    var data = listOf<Profile>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, currProfile)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, resources)
        holder.itemView.setOnClickListener {
            val mainActivity = frag.activity as MainActivity
//            mainActivity.groupMatchingSelectedProfile = data[position]
//            mainActivity.switchFragment(MatchingProfileFragment(), true)
            mainActivity.switchFragment(ProfileFragment(), true)
        }
    }

    class ViewHolder private constructor(itemView: View, private val currUser: LiveData<Profile>) : RecyclerView.ViewHolder(itemView) {
        private val avatarIcon: ImageView = itemView.findViewById(R.id.avatar_icon)
        private val nameTextView: TextView = itemView.findViewById(R.id.name_textview)
        private val statusTextView: TextView = itemView.findViewById(R.id.status_textview)
        private val starButton: ImageButton = itemView.findViewById(R.id.star_button_leaderboard)
        private val profileMatch: TextView = itemView.findViewById(R.id.profile_match)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.description_textview)
        private lateinit var context: Context

        companion object {
            fun from(parent: ViewGroup, profile: LiveData<Profile>): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.group_matching_tile, parent, false)
                val viewHolder = ViewHolder(view, profile)
                viewHolder.context = parent.context
                return viewHolder
            }
        }

        fun bind(item: Profile, resources: Resources) {
            nameTextView.text = item.firstName + " " + item.lastName

            val teamStatusArray = resources.getStringArray(R.array.team_status_array)
            val teamStatusVerboseArray = resources.getStringArray(R.array.team_status_verbose_array)
            val teamStatusColors = arrayOf(R.color.lookingForTeamColor, R.color.lookingForMembersColor, R.color.notLookingColor)
//            statusTextView.text = item.teamStatus
//            val index: Int? = teamStatusArray.indices.firstOrNull { i -> teamStatusArray[i] == item.teamStatus }
//            index?.let {
//                statusTextView.text = "⬤ " + teamStatusVerboseArray[index]
//                statusTextView.setTextColor(resources.getColor(teamStatusColors[index]))
//            }

            profileMatch.text = item.discord
//            descriptionTextView.text = item.description
            starButton.isSelected = FavoritesManager.isFavoritedProfile(context, item)
            if (item.id == currUser.value?.id) {
                starButton.visibility = View.GONE
            } else {
                starButton.visibility = View.VISIBLE
                starButton.setOnClickListener {
                    starButton.isSelected = !starButton.isSelected
                    if (starButton.isSelected) {
                        FavoritesManager.favoriteProfile(context, item)
                        Snackbar.make(starButton, R.string.profile_favorited_notif, Snackbar.LENGTH_SHORT).show()
                    } else {
                        FavoritesManager.unfavoriteProfile(context, item)
                    }
                }
            }
            try {
                Glide.with(context)
                    .load(item.avatarUrl)
                    .apply(RequestOptions().transforms(CenterCrop(), RoundedCorners(16)))
                    .into(avatarIcon)
                avatarIcon.setBackgroundResource(0)
            } catch (e: Exception) {
                Log.e("GroupAdapter", e.localizedMessage)
            }
        }
    }
}
