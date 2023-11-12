package org.hackillinois.android.view.schedule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.event_tile.view.*
import kotlinx.android.synthetic.main.time_list_item.view.*
import org.hackillinois.android.R
import org.hackillinois.android.common.FavoritesManager
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.view.MainActivity
import org.hackillinois.android.view.eventinfo.EventInfoFragment

class EventsAdapter(
    private var itemList: List<ScheduleListItem>,
    private val eventClickListener: EventClickListener
) : RecyclerView.Adapter<EventsAdapter.ViewHolder>(), EventClickListener {
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

            eventTimeSpanText.text = when (event.isAsync) {
                false -> "${event.getStartTimeOfDay()} - ${event.getEndTimeOfDay()}"
                true -> "Asynchronous event"
            }
            sponsoredTextView.text = "Sponsored by ${event.sponsor}"
            sponsoredTextView.visibility = if (event.sponsor.isEmpty()) View.GONE else View.VISIBLE
            sponsored_marker.visibility = if (event.sponsor.isEmpty()) View.GONE else View.VISIBLE
            if (event.locations.isEmpty()) {
                locationTextView.text = "N/A"
            } else {
                // add multiple locations
                val locationText = StringBuilder()
                for (i in event.locations.indices) {
                    if (i != 0) locationText.append(",  ")
                    locationText.append(event.locations[i].description)
                }
                locationTextView.text = locationText
            }
            eventDescriptionTextView.text = event.description
            pointsView.text = " + ${event.points} pts "

            // @todo sloppy, clean up
            when (event.eventType) {
                "MEAL" -> {
                    eventType.setText(R.string.mealText)
                }
                "SPEAKER" -> {
                    eventType.setText(R.string.speakerText)
                }
                "WORKSHOP" -> {
                    eventType.setText(R.string.workshopText)
                }
                "MINIEVENT" -> {
                    eventType.setText(R.string.miniEventText)
                }
                "QNA" -> {
                    eventType.setText(R.string.qnaText)
                }
                "OTHER" -> {
                    eventType.setText(R.string.otherText)
                }
                else -> {
                    eventType.visibility = View.GONE
                }
            }
            starButton.isSelected = FavoritesManager.isFavoritedEvent(context, event.id)
            starButton.setOnClickListener { button ->
                button.isSelected = !button.isSelected

                if (button.isSelected) {
                    FavoritesManager.favoriteEvent(context, event)
                    val toast = Toast.makeText(context, R.string.schedule_snackbar_notifications_on, Toast.LENGTH_SHORT)
                    toast.show()
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

    override fun openEventInfoActivity(event: Event) {
        val eventInfoFragment = EventInfoFragment.newInstance(event.id)
        (context as MainActivity).switchFragment(eventInfoFragment, true)
    }
}
