package org.hackillinois.android.view.home.eventlist

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import kotlinx.android.synthetic.main.event_list_header.view.*
import kotlinx.android.synthetic.main.event_tile.view.*
import org.hackillinois.android.R
import org.hackillinois.android.common.FavoritesManager
import org.hackillinois.android.database.entity.Event

class EventsSection(
    var eventsList: List<Event>,
    private val headerText: String,
    private val headerColor: Int,
    private val showTime: Boolean,
    private val eventClickListener: EventClickListener,
    private val context: Context
) :
    Section(
        SectionParameters.builder()
                .itemResourceId(R.layout.event_tile)
                .headerResourceId(R.layout.event_list_header)
                .build()
) {

    override fun getContentItemsTotal() = eventsList.size

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val event = eventsList[position]
        holder.itemView.apply {
            titleTextView.text = event.name
            sponsoredTextView.text = "Sponsored by ${event.sponsor}"
            sponsoredTextView.visibility = if (event.sponsor.isEmpty()) View.GONE else View.VISIBLE
            eventLocationTextView.text = event.getLocationDescriptionsAsString()
            eventDescriptionTextView.text = event.description

            setOnClickListener {
                eventClickListener.openEventInfoActivity(eventsList[position])
            }

            starButton.isSelected = FavoritesManager.isFavoritedEvent(context, event.id)
            starButton.setOnClickListener(starClickListener(event))
        }
    }

    private fun starClickListener(event: Event) = View.OnClickListener { button ->
        button.isSelected = !button.isSelected

        if (button.isSelected) {
            FavoritesManager.favoriteEvent(context, event)
            Snackbar.make(button, R.string.schedule_snackbar_notifications_on, Snackbar.LENGTH_SHORT).show()
        } else {
            FavoritesManager.unfavoriteEvent(context, event)
        }
    }

    override fun getItemViewHolder(view: View) = BasicViewHolder(view)
    override fun getHeaderViewHolder(view: View) = BasicViewHolder(view)

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder) {
        holder.itemView.apply {
            headerText.setTextColor(headerColor)
            headerText.text = (this@EventsSection).headerText
            headerText.visibility = if (eventsList.isNotEmpty()) View.VISIBLE else View.GONE

            timeText.text = eventsList.firstOrNull()?.getStartTimeOfDay()
            timeText.visibility = if (showTime && eventsList.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    fun updateEventsList(newEventsList: List<Event>) {
        eventsList = newEventsList
    }
}
