package org.hackillinois.android.view

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.mentor_recycleview.view.*
import org.hackillinois.android.R
import org.hackillinois.android.common.FavoritesManager
import org.hackillinois.android.model.mentor.MentorModel
import org.hackillinois.android.view.schedule.EventsAdapter

class MentorAdapter internal constructor(private val mentorList: ArrayList<MentorModel>) : RecyclerView.Adapter<MentorAdapter.ViewHolder> {

    private lateinit var context: Context


    inner class ViewHolder(parent: View) : RecyclerView.ViewHolder(parent)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mentor_recycleview, parent, false)
        val viewHolder = ViewHolder(view)
        context = parent.context
        return viewHolder
    }

    override fun onBindViewHolder(holder: EventsAdapter.ViewHolder, position: Int) {
        val mentor = mentorList[position]

        /*holder.itemView.apply {
            constraintLayout_event_onclick.setOnClickListener { view ->
                val context = view.context
                val intent = Intent(context, EventInfoActivity::class.java)
                intent.putExtra("event_name", event.name)
                context.startActivity(intent)
            }*/

            mentorName.text = mentor.name
            mentorNumber.text = "#" + mentor.number
            mentorLocation.text = mentor.location
            favoriteMentor.isSelected = FavoritesManager.isFavorited(context, mentor.name)
        }

    override fun getItemCount() = mentorList.size
}