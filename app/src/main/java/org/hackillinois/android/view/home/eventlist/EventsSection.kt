package org.hackillinois.android.view.home.eventlist

import android.content.Context
import android.graphics.Color
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
    private val headerColor: Int = Color.BLACK,
    private val showTime: Boolean,
    private val eventClickListener: EventClickListener,
    private val context: Context,
) :
    Section(
        SectionParameters.builder()
            .itemResourceId(R.layout.event_tile)
            .build(),
    ) {

    override fun getContentItemsTotal() = eventsList.size

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val event = eventsList[position]
        holder.itemView.apply {
            titleTextView.text = event.name
//            sponsoredTextView.text = "Sponsored by ${event.sponsor}"
//            sponsoredTextView.visibility = if (event.sponsor.isEmpty()) View.GONE else View.VISIBLE
            eventTimeSpanText.text = "${event.getStartTimeOfDay()} - ${event.getEndTimeOfDay()}"
//            eventLocationTextView.text = event.getLocationDescriptionsAsString()
            if (event.description.length <= 107) {
                eventDescriptionTextView.text = event.description
            } else {
                eventDescriptionTextView.text = event.description.substring(0, 107) + "..."
            }
            pointsView.text = " +${event.points} pts "
            eventDescriptionTextView.text = event.description
            pointsView.text = "${event.points} Points!"

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

            // iOS doesn't have this -- Hack 2021
            setOnClickListener {
                eventClickListener.openEventInfoActivity(eventsList[position])
            }

            starButton.isSelected = FavoritesManager.isFavoritedEvent(context, event.id)
            starButton.setOnClickListener(starClickListener(event))

            timeText.text = eventsList[position].getStartTimeOfDay()
            timeText.visibility = if (showTime && eventsList.isNotEmpty()) View.VISIBLE else View.GONE
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
        }
    }

    fun updateEventsList(newEventsList: List<Event>) {
        eventsList = newEventsList
    }
}
