package org.hackillinois.android.view.schedule

import android.app.Application
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.LiveData
import kotlinx.android.synthetic.main.fragment_schedule.view.*
import org.hackillinois.android.R
import org.hackillinois.android.common.FavoritesManager
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.view.home.eventlist.EventClickListener
import org.hackillinois.android.view.schedule.EventsAdapter.ViewHolder
import org.hackillinois.android.viewmodel.EventInfoViewModel
import org.hackillinois.android.viewmodel.ScheduleViewModel

class ScheduleFragment(val app: Application) : Fragment() {

    private lateinit var favoriteButton: Button

    lateinit var event: LiveData<Event>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)

        val sectionsPagerAdapter = SectionsPagerAdapter(childFragmentManager)

        view.scheduleContainer.adapter = sectionsPagerAdapter
        view.scheduleContainer.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(view.scheduleDays))
        view.scheduleDays.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(view.scheduleContainer))

        favoriteButton = view.findViewById(R.id.favScheduleButton)

        favoriteButton.setOnClickListener(favScheduleClickListener)

        val scheduleViewModel = ScheduleViewModel()



        val time = System.currentTimeMillis()

        view.scheduleContainer.currentItem = when {
            time < scheduleViewModel.fridayEnd -> 0
            time < scheduleViewModel.saturdayEnd -> 1
            time < scheduleViewModel.sundayEnd -> 2
            else -> 0
        }

        return view
    }

    inner class SectionsPagerAdapter constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int) = DayFragment.newInstance(position)
        override fun getCount() = 3
    }



    private val favScheduleClickListener = OnClickListener {
        favoriteButton.isSelected = true

        //filterFavEvents(EventsAdapter.itemList)
    }

    private fun filterFavEvents(eventList: List<Event>): List<ScheduleListItem> {
        eventList.forEach {
            if (event.value?.name?.let { it1 -> FavoritesManager.isFavorited(app.applicationContext, it1) }!!) {
                // keep in event list
            } else {
                // remove from event list
            }
        }
        return eventList
    }
}
