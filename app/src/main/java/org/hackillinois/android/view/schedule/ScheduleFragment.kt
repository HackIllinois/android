package org.hackillinois.android.view.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_schedule.view.*
import org.hackillinois.android.R
import org.hackillinois.android.viewmodel.ScheduleViewModel

class ScheduleFragment : Fragment() {

    private lateinit var favoriteButton: ImageButton
    private lateinit var scheduleViewModel: ScheduleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scheduleViewModel = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)
        // Observe "Favorites" LiveData
        scheduleViewModel.showFavorites.observe(
            this,
            Observer {
                favoriteButton.isSelected = it ?: false
            },
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)

        // Link tab/day selection to the ViewPager
        view.scheduleContainer.adapter = SectionsPagerAdapter(childFragmentManager)
        view.scheduleContainer.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(view.scheduleDays))
        view.scheduleDays.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(view.scheduleContainer))

        favoriteButton = view.findViewById(R.id.lightBookmarkButton)
        favoriteButton.setOnClickListener(favScheduleClickListener)

        // If hackathon is underway, change tab to current day
        val time = System.currentTimeMillis()
        view.scheduleContainer.currentItem = when {
            time < scheduleViewModel.fridayEnd -> 0
            time < scheduleViewModel.saturdayEnd -> 1
            time < scheduleViewModel.sundayEnd -> 2
            else -> 0
        }

        return view
    }

    // Construct DayFragments for ViewPager
    inner class SectionsPagerAdapter constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int) = DayFragment.newInstance(position)
        override fun getCount() = 3
        override fun getPageTitle(position: Int) =
            when (position) {
                0 -> "FRIDAY"
                1 -> "SATURDAY"
                else -> "SUNDAY"
            }
    }

    // Update "Favorites" ViewModel on click
    private val favScheduleClickListener = OnClickListener {
        favoriteButton.apply {
            isSelected = !favoriteButton.isSelected
            setImageResource(
                when (isSelected) {
                    true -> R.drawable.light_bookmark_filled
                    else -> R.drawable.light_bookmark_hollow
                },
            )
        }
        scheduleViewModel.showFavorites.postValue(favoriteButton.isSelected)
    }
}
