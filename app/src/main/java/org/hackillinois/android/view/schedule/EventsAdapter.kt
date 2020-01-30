package org.hackillinois.android.view.schedule

import android.content.Context
import android.content.Intent
import com.google.android.material.snackbar.Snackbar
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.event_tile.view.*
import org.hackillinois.android.R
import org.hackillinois.android.common.FavoritesManager
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.view.EventInfoFragment
import org.hackillinois.android.view.home.eventlist.EventClickListener

class EventsAdapter(
    private val eventList: List<Event>,
    private val eventClickListener: EventClickListener
) : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {
    private lateinit var context: Context

    inner class ViewHolder(parent: View) : RecyclerView.ViewHolder(parent)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_tile, parent, false)
        val viewHolder = ViewHolder(view)
        context = parent.context
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = eventList[position]

        holder.itemView.apply {
            setOnClickListener { view ->
                eventClickListener.openEventInfoActivity(event)
            }

            titleTextView.text = event.name
            eventLocationTextView.text = event.getLocationDescriptionsAsString()
            starButton.isSelected = FavoritesManager.isFavorited(context, event.name)
            starButton.setOnClickListener { button ->
                button.isSelected = !button.isSelected

                if (button.isSelected) {
                    FavoritesManager.favoriteEvent(context, event)
                    Snackbar.make(button, R.string.schedule_snackbar_notifications_on, Snackbar.LENGTH_SHORT).show()
                } else {
                    FavoritesManager.unfavoriteEvent(context, event)
                }
            }
        }
    }

    override fun getItemCount() = eventList.size
}