package org.hackillinois.android.view.schedule

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.event_tile.view.*
import kotlinx.android.synthetic.main.shift_tile.view.*
import kotlinx.android.synthetic.main.time_list_item.view.*
import org.hackillinois.android.API
import org.hackillinois.android.App
import org.hackillinois.android.R
import org.hackillinois.android.common.FavoritesManager
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.database.entity.Shift
import org.hackillinois.android.model.scanner.EventId
import org.hackillinois.android.model.user.FavoritesResponse
import org.hackillinois.android.view.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventsAdapter(
    private var itemList: List<ScheduleListItem>,
    private val eventClickListener: EventClickListener,
    private val isAttendeeViewing: Boolean
) : RecyclerView.Adapter<EventsAdapter.ViewHolder>(), EventClickListener {
    private lateinit var context: Context

    inner class ViewHolder(parent: View) : RecyclerView.ViewHolder(parent)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutResource = when (viewType) {
            1 -> R.layout.event_tile
            2 -> R.layout.shift_tile
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
        } else if (item.getType() == 2) {
            val shift = item as Shift
            bindShiftItem(shift, holder.itemView)
        } else {
            val timeListItem = item as TimeListItem
            bindTimeItem(timeListItem, holder.itemView)
        }
    }

    private fun bindShiftItem(shift: Shift, itemView: View) {
        itemView.apply {
            // TODO: figure out how to update with the time like Leah's home page
            shiftOngoingTextView.visibility = if (shift.isCurrentlyHappening()) View.VISIBLE else View.GONE
            shiftCoverSquare.visibility = if (shift.isCurrentlyHappening()) View.VISIBLE else View.GONE

            shiftTitleTextView.text = shift.name

            shiftTimeSpanText.text = when (shift.isAsync) {
                false -> "${shift.getStartTimeOfDay()} - ${shift.getEndTimeOfDay()}"
                true -> "Asynchronous shift"
            }
            if (shift.locations.isEmpty()) {
                shiftLocationTextView.text = "N/A"
            } else {
                // add multiple locations
                val locationText = StringBuilder()
                for (i in shift.locations.indices) {
                    if (i != 0) locationText.append(",  ")
                    locationText.append(shift.locations[i].description)
                }
                shiftLocationTextView.text = locationText
            }
            shiftDescriptionTextView.text = shift.description
        }
    }

    private fun bindEventItem(event: Event, itemView: View) {
        itemView.apply {
            setOnClickListener {
                eventClickListener.openEventInfoActivity(event)
            }

            if (!isAttendeeViewing) {
                bookmarkButton.visibility = View.GONE
            }

            // TODO: figure out how to update with the time like Leah's home page
            ongoingTextView.visibility = if (event.isCurrentlyHappening()) View.VISIBLE else View.GONE
            coverSquare.visibility = if (event.isCurrentlyHappening()) View.VISIBLE else View.GONE

            proTag.visibility = if (event.isPro) View.VISIBLE else View.GONE

            titleTextView.text = event.name

            eventTimeSpanText.text = when (event.isAsync) {
                false -> "${event.getStartTimeOfDay()} - ${event.getEndTimeOfDay()}"
                true -> "Asynchronous event"
            }
            if (event.sponsor == null || event.sponsor == "" || event.sponsor == "None") {
                sponsoredTextView.visibility = View.GONE
                sponsored_marker.visibility = View.GONE
            } else {
                sponsoredTextView.text = "Sponsored by ${event.sponsor}"
                sponsoredTextView.visibility = View.VISIBLE
                sponsored_marker.visibility = View.VISIBLE
            }
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
            pointsView.text = "+ ${event.points} pts"

            // format and show eventType
            if (event.eventType == "QNA") {
                eventType.text = "Q&A"
            } else {
                val eventTypeString = event.eventType.lowercase()
                eventType.text = eventTypeString.replaceFirst(eventTypeString.first(), eventTypeString.first().uppercaseChar())
            }

            // handle favoriting the bookmark and api calls
            bookmarkButton.isSelected = FavoritesManager.isFavoritedEvent(context, event.eventId)
            bookmarkButton.setOnClickListener { button ->
                button.isSelected = !button.isSelected

                val api: API = App.getAPI()
                val call: Call<FavoritesResponse>
                if (button.isSelected) {
                    FavoritesManager.favoriteEvent(context, event)
                    call = api.followEvent(EventId(event.eventId))
                    Log.d("following eventId:", event.eventId)
                    val toast = Toast.makeText(context, R.string.schedule_snackbar_notifications_on, Toast.LENGTH_SHORT)
                    toast.show()
                } else {
                    FavoritesManager.unfavoriteEvent(context, event)
                    call = api.unfollowEvent(EventId(event.eventId))
                }

                call.enqueue(object : Callback<FavoritesResponse> {
                    override fun onResponse(call: Call<FavoritesResponse>, response: Response<FavoritesResponse>) {
                        if (response.isSuccessful) {
                            val responseData: FavoritesResponse? = response.body()
                        }
                    }

                    override fun onFailure(call: Call<FavoritesResponse>, t: Throwable) {
                        Log.d("FOLLOW/UNFOLLOW FAILURE", t.toString())
                    }
                })
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
        val eventInfoFragment = EventInfoFragment.newInstance(event.eventId, isAttendeeViewing)
        (context as MainActivity).switchFragment(eventInfoFragment, true)
    }
}
