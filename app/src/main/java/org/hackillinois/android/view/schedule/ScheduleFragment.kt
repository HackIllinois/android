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

    // owo sneaky comment >w<
    private lateinit var favoriteButton: ImageButton
    private lateinit var scheduleViewModel: ScheduleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scheduleViewModel = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)
        scheduleViewModel.showFavorites.observe(
            this,
            Observer {
                favoriteButton.isSelected = it ?: false
            }
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)

        val sectionsPagerAdapter = SectionsPagerAdapter(childFragmentManager)

        view.scheduleContainer.adapter = sectionsPagerAdapter
        view.scheduleContainer.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(view.scheduleDays))
        view.scheduleDays.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(view.scheduleContainer))

        favoriteButton = view.findViewById(R.id.StarButton)

        favoriteButton.setOnClickListener(favScheduleClickListener)

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
        override fun getPageTitle(position: Int) =
            when (position) {
                0 -> "FRIDAY"
                1 -> "SATURDAY"
                else -> "SUNDAY"
            }
    }

    private val favScheduleClickListener = OnClickListener {
        favoriteButton.apply {
            isSelected = !favoriteButton.isSelected
            setImageResource(
                when (isSelected) {
                    true -> R.drawable.ic_star_filled
                    else -> R.drawable.ic_star_selectable
                }
            )
        }
        scheduleViewModel.showFavorites.postValue(favoriteButton.isSelected)
    }
}
