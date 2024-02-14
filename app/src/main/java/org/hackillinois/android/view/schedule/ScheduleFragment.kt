package org.hackillinois.android.view.schedule

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_schedule.scheduleDays
import kotlinx.android.synthetic.main.fragment_schedule.view.*
import org.hackillinois.android.R
import org.hackillinois.android.common.JWTUtilities
import org.hackillinois.android.viewmodel.ScheduleViewModel

class ScheduleFragment : Fragment() {

    private lateinit var shift_header: TextView
    private lateinit var schedule_header: TextView
    private lateinit var favoriteButton: ImageButton
    private lateinit var scheduleViewModel: ScheduleViewModel
    private lateinit var scheduleBackground: ImageView
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
                schedule_header.text = if (it) "Saved Events" else "Schedule"
            },
        )

        scheduleViewModel.showShifts.observe(
            this,
            Observer {
                shift_header.isSelected = it ?: false
            },
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)

        Log.d("ONCREATEVIEW", "")

        // Link tab/day selection to the ViewPager
        view.scheduleContainer.adapter = SectionsPagerAdapter(childFragmentManager)
        view.scheduleContainer.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(view.scheduleDays))
        view.scheduleDays.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(view.scheduleContainer))
        setupCustomTabs(view.scheduleDays)
        favoriteButton = view.findViewById(R.id.lightBookmarkButton)
        schedule_header = view.findViewById(R.id.schedule_header)
        shift_header = view.findViewById(R.id.shift_header)
        scheduleBackground = view.findViewById(R.id.scheduleBackground)
        scheduleBackground.setImageResource(R.drawable.dark_fantasy_bg_2024)
        // TODO: Check if Guests should be able to fav
        if (isStaff() || !hasLoggedIn()) {
            Log.d("ISSTAFF", scheduleViewModel.isAttendeeViewing.toString())
            scheduleViewModel.isAttendeeViewing = false
            favoriteButton.visibility = View.GONE
        } else {
            Log.d("ISATTENDEE", scheduleViewModel.isAttendeeViewing.toString())
            scheduleViewModel.isAttendeeViewing = true
            favoriteButton.visibility = View.VISIBLE
            favoriteButton.setOnClickListener(favScheduleClickListener)
        }
        if (isStaff()) {
            shift_header.visibility = View.VISIBLE
            val context = requireActivity().applicationContext
            schedule_header.background = ContextCompat.getDrawable(context, R.drawable.schedule_underline)
            shift_header.setOnClickListener(shiftScheduleClickListener)
            schedule_header.setOnClickListener(eventScheduleClickListener)
        } else {
            shift_header.visibility = View.GONE
            schedule_header.setBackgroundResource(0)
        }

        // If hackathon is underway, change tab to current day
        val time = System.currentTimeMillis()
        view.scheduleContainer.currentItem = when {
            time < scheduleViewModel.fridayEnd -> 0
            time < scheduleViewModel.saturdayEnd -> 1
            time < scheduleViewModel.sundayEnd -> 2
            else -> 0
        }
        view.scheduleDays.getTabAt(view.scheduleContainer.currentItem)?.customView?.background =
            context?.let { ContextCompat.getDrawable(it, R.drawable.tab_selected) }

        return view
    }

    private fun setupCustomTabs(tabLayout: TabLayout) {
        val tabDayOfMonth = arrayOf("23", "24", "25")
        val tabDayOfWeek = arrayOf("FRI", "SAT", "SUN")

        for (i in tabDayOfMonth.indices) {
            val tab = tabLayout.newTab()
            tab.customView = createTabView(tabDayOfMonth[i], tabDayOfWeek[i])
            tabLayout.addTab(tab)
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabDrawable = if (showingShifts) R.drawable.tab_selected_light else R.drawable.tab_selected
                tab.customView?.background = context?.let { ContextCompat.getDrawable(it, tabDrawable) }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {
                val tabDrawable = if (showingShifts) R.drawable.tab_unselected_light else R.drawable.tab_unselected
                tab.customView?.background = context?.let { ContextCompat.getDrawable(it, tabDrawable) }
            }
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun createTabView(dayOfMonth: String, dayOfWeek: String): View {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_tab, null)
        val dayOfMonthView = view.findViewById<TextView>(R.id.tab_day_of_month)
        val dayOfWeekView = view.findViewById<TextView>(R.id.tab_day_of_week)
        dayOfMonthView.text = dayOfMonth
        dayOfWeekView.text = dayOfWeek
        return view
    }

    // Construct DayFragments for ViewPager
    inner class SectionsPagerAdapter constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int) = DayFragment.newInstance(position)
        override fun getCount() = 3
        override fun getPageTitle(position: Int): CharSequence? { return null }
    }

    override fun onResume() {
        super.onResume()
        favoriteButton.setImageResource(if (showingFavorites) R.drawable.light_bookmark_filled else R.drawable.light_bookmark_hollow)
        schedule_header.text = if (showingFavorites) "Saved Events" else "Schedule"
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
        showingFavorites = favoriteButton.isSelected
    }

    private val shiftScheduleClickListener = OnClickListener {
        // Log.d("shift_header.isSelected", "${shift_header.isSelected}")
        shift_header.setBackgroundResource(R.drawable.shift_underline)
        schedule_header.setBackgroundResource(0)
        schedule_header.setTextColor(getResources().getColor(R.color.burntBark))
        shift_header.setTextColor(getResources().getColor(R.color.burntBark))
        scheduleBackground.setImageResource(R.drawable.light_fantasy_bg_2024)
        for (i in 0 until scheduleDays.tabCount) {
            val tab = scheduleDays.getTabAt(i)
            val tabDrawable = if (tab?.isSelected == true) R.drawable.tab_selected_light else R.drawable.tab_unselected_light
            tab?.customView?.background = context?.let { ContextCompat.getDrawable(it, tabDrawable) }
        }
        scheduleViewModel.showShifts.postValue(true)
        showingShifts = true
    }

    private val eventScheduleClickListener = OnClickListener {
        // Log.d("shift_header.isSelected", "${shift_header.isSelected}")
        shift_header.setBackgroundResource(0)
        schedule_header.setBackgroundResource(R.drawable.schedule_underline)
        schedule_header.setTextColor(getResources().getColor(R.color.white))
        shift_header.setTextColor(getResources().getColor(R.color.white))
        scheduleBackground.setImageResource(R.drawable.dark_fantasy_bg_2024)
        for (i in 0 until scheduleDays.tabCount) {
            val tab = scheduleDays.getTabAt(i)
            val tabDrawable = if (tab?.isSelected == true) R.drawable.tab_selected else R.drawable.tab_unselected
            tab?.customView?.background = context?.let { ContextCompat.getDrawable(it, tabDrawable) }
        }
        scheduleViewModel.showShifts.postValue(false)
        showingShifts = false
    }

    private fun isStaff(): Boolean {
        val context = requireActivity().applicationContext
        val prefString = context.getString(R.string.authorization_pref_file_key)
        return context.getSharedPreferences(prefString, Context.MODE_PRIVATE).getString("provider", "") ?: "" == "google"
    }

    private fun hasLoggedIn(): Boolean {
        // Reads JWT and checks if it is equal to an empty JWT
        return JWTUtilities.readJWT(requireActivity().applicationContext) != JWTUtilities.DEFAULT_JWT
    }
}
