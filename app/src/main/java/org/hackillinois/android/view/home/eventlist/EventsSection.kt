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
    private val eventClickListener: EventClickListener,
    private val context: Context
)
    : Section(
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
            eventLocationTextView.text = event.getLocationDescriptionsAsString()
            eventDescriptionTextView.text = event.description
        }

        holder.itemView.setOnClickListener {
            eventClickListener.openEventInfoActivity(eventsList[position])
        }

        holder.itemView.starButton.isSelected = FavoritesManager.isFavorited(context, event.name)
        holder.itemView.starButton.setOnClickListener { button ->
            button.isSelected = !button.isSelected

            if (button.isSelected) {
                FavoritesManager.favoriteEvent(context, event)
                Snackbar.make(button, R.string.schedule_snackbar_notifications_on, Snackbar.LENGTH_SHORT).show()
            } else {
                FavoritesManager.unfavoriteEvent(context, event)
            }
        }
    }

    override fun getItemViewHolder(view: View) = BasicViewHolder(view)
    override fun getHeaderViewHolder(view: View) = BasicViewHolder(view)

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder) {
        holder.itemView.headerText.setTextColor(headerColor)
        holder.itemView.headerText.text = headerText
    }

    fun updateEventsList(newEventsList: List<Event>) {
        eventsList = newEventsList
    }
}

