package org.hackillinois.android.view.schedule

import android.content.Context
import com.google.android.material.snackbar.Snackbar
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.event_tile.view.*
import kotlinx.android.synthetic.main.time_list_item.view.*
import org.hackillinois.android.R
import org.hackillinois.android.common.FavoritesManager
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.view.home.eventlist.EventClickListener

class EventsAdapter(
    private var itemList: List<ScheduleListItem>,
    private val eventClickListener: EventClickListener
) : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {
    private lateinit var context: Context

    inner class ViewHolder(parent: View) : RecyclerView.ViewHolder(parent)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutResource = when (viewType) {
            1 -> R.layout.event_tile
            else -> R.layout.time_list_item
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutResource, parent, false)
        val viewHolder = ViewHolder(view)
        context = parent.context
        return viewHolder
    }

    override fun getItemViewType(position: Int): Int {
        return itemList[position].getType()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]

        if (item.getType() == 1) {
            val event = item as Event
            bindEventItem(event, holder.itemView)
        } else {
            val timeListItem = item as TimeListItem
            bindTimeItem(timeListItem, holder.itemView)
        }
    }

    private fun bindEventItem(event: Event, itemView: View) {
        itemView.apply {
            setOnClickListener { eventClickListener.openEventInfoActivity(event) }

            titleTextView.text = event.name
            sponsoredTextView.text = "Sponsored by ${event.sponsor}"
            sponsoredTextView.visibility = if (event.sponsor.isEmpty()) View.GONE else View.VISIBLE

            eventLocationTextView.text = event.getLocationDescriptionsAsString()
            eventDescriptionTextView.text = event.description
            starButton.isSelected = FavoritesManager.isFavoritedEvent(context, event.id)
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

    private fun bindTimeItem(timeListItem: TimeListItem, itemView: View) {
        itemView.timeTextView.text = timeListItem.timeString
    }

    override fun getItemCount() = itemList.size

    fun updateEvents(list: List<ScheduleListItem>) {
        this.itemList = list
        notifyDataSetChanged()
    }
}