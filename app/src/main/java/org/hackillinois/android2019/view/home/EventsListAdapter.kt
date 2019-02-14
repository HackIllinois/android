package org.hackillinois.android2019.view.home

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.home_event_list_item.view.*
import org.hackillinois.android2019.R
import org.hackillinois.android2019.database.entity.Event

class EventsListAdapter(var eventsList: List<Event>, private val eventClickListener: EventClickListener) : RecyclerView.Adapter<EventsListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_event_list_item, parent, false)
        return EventsListViewHolder(view)
    }

    override fun getItemCount() = eventsList.size

    override fun onBindViewHolder(holder: EventsListViewHolder, position: Int) {
        val event = eventsList[position]
        holder.view.apply {
            eventName.text = event.name
            location.text = event.getLocationDescriptionsAsString()
        }

        holder.view.setOnClickListener {
            eventClickListener.openEventInfoActivity(eventsList[position])
        }
    }

    fun updateEventsList(newEventsList: List<Event>) {
        eventsList = newEventsList
        notifyDataSetChanged()
    }

    interface EventClickListener {
        fun openEventInfoActivity(event: Event)
    }
}
