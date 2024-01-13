package org.hackillinois.android.view.schedule

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginStart
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_schedule.schedule_header
import kotlinx.android.synthetic.main.fragment_schedule.view.*
import org.hackillinois.android.R
import org.hackillinois.android.viewmodel.ScheduleViewModel

class ScheduleFragment : Fragment() {

    private lateinit var shift_header: TextView
    private lateinit var schedule_header: TextView
    private lateinit var favoriteButton: ImageButton
    private lateinit var scheduleViewModel: ScheduleViewModel
    private var showingFavorites: Boolean = false
    private var showingShifts: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scheduleViewModel = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)
        // Observe "Favorites" LiveData
        scheduleViewModel.showFavorites.observe(
            this,
            Observer {
                favoriteButton.isSelected = it ?: false
            }
        )

        scheduleViewModel.showShifts.observe(
            this,
            Observer {
                shift_header.isSelected = it ?: false
            }
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)

        // Link tab/day selection to the ViewPager
        view.scheduleContainer.adapter = SectionsPagerAdapter(childFragmentManager)
        view.scheduleContainer.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(view.scheduleDays))
        view.scheduleDays.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(view.scheduleContainer))

        favoriteButton = view.findViewById(R.id.lightBookmarkButton)
        schedule_header = view.findViewById(R.id.schedule_header)
        shift_header = view.findViewById(R.id.shift_header)
        // TODO: Check if Guests should be able to fav
        if (isStaff()) {
            favoriteButton.visibility = View.GONE
            shift_header.visibility = View.VISIBLE
            val context = requireActivity().applicationContext
            (schedule_header.layoutParams as ViewGroup.MarginLayoutParams).marginStart = (50 * resources.displayMetrics.density).toInt()
            schedule_header.background = ContextCompat.getDrawable(context, R.drawable.schedule_underline)
            shift_header.setOnClickListener(shiftScheduleClickListener)
            schedule_header.setOnClickListener(eventScheduleClickListener)
        } else {
            favoriteButton.visibility = View.VISIBLE
            shift_header.visibility = View.GONE
            schedule_header.setBackgroundResource(0)
            (schedule_header.layoutParams as ViewGroup.MarginLayoutParams).marginStart = (30 * resources.displayMetrics.density).toInt()
            favoriteButton.setOnClickListener(favScheduleClickListener)
        }

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

    override fun onResume() {
        super.onResume()
        if (showingFavorites) {
            favoriteButton.setImageResource(R.drawable.light_bookmark_filled)
        } else {
            favoriteButton.setImageResource(R.drawable.light_bookmark_hollow)
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
                }
            )
        }
        scheduleViewModel.showFavorites.postValue(favoriteButton.isSelected)
        showingFavorites = favoriteButton.isSelected
    }

    private val shiftScheduleClickListener = OnClickListener {
        // Log.d("shift_header.isSelected", "${shift_header.isSelected}")
        shift_header.setBackgroundResource(R.drawable.schedule_underline)
        schedule_header.setBackgroundResource(0)
        scheduleViewModel.showShifts.postValue(true)
        showingShifts = true
    }

    private val eventScheduleClickListener = OnClickListener {
        // Log.d("shift_header.isSelected", "${shift_header.isSelected}")
        shift_header.setBackgroundResource(0)
        schedule_header.setBackgroundResource(R.drawable.schedule_underline)
        scheduleViewModel.showShifts.postValue(false)
        showingShifts = false
    }

    private fun isStaff(): Boolean {
        val context = requireActivity().applicationContext
        val prefString = context.getString(R.string.authorization_pref_file_key)
        return context.getSharedPreferences(prefString, Context.MODE_PRIVATE).getString("provider", "") ?: "" == "google"
    }
}
