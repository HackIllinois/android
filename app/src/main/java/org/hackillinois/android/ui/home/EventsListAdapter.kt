package org.hackillinois.android.ui.home

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.home_event_list_item.view.*
import org.hackillinois.android.R
import org.hackillinois.android.model.Event

class EventsListAdapter(var eventsList: List<Event>) : RecyclerView.Adapter<EventsListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_event_list_item, parent, false)
        return EventsListViewHolder(view)
    }

    override fun getItemCount() = eventsList.size

    override fun onBindViewHolder(holder: EventsListViewHolder, position: Int) {
        val event = eventsList[position]
        holder.view.apply {
            eventName.text = event.name
            location.text = event.locationDescription
        }
    }

    fun updateEventsList(newEventsList: List<Event>) {
        eventsList = newEventsList
        notifyDataSetChanged()
    }
}
