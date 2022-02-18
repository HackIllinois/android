package org.hackillinois.android.view.maps.indoor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_indoor_maps.*
import kotlinx.android.synthetic.main.fragment_indoor_maps.view.*
import kotlinx.android.synthetic.main.fragment_schedule.view.*
import org.hackillinois.android.R

class IndoorMapsFragment : Fragment() {

    private var index = 0

    private lateinit var pagerAdapter: FragmentPagerAdapter

    private var tabs: TabLayout? = null
    private var indoorMapsContainer: ViewPager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_indoor_maps, container, false)
        pagerAdapter = IndoorMapsPagerAdapter(childFragmentManager)
        indoorMapsContainer = view.indoorMapsContainer
        view.indoorMapsContainer.adapter = pagerAdapter

        tabs?.let {
            view.indoorMapsContainer.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(it))
            it.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(view.indoorMapsContainer))
        }

        changeFragment(index)
        return view
    }

    fun onTabSelected(index: Int) {
        changeFragment(index)
    }

    private fun changeFragment(index: Int) {
        this.index = index
        indoorMapsContainer?.currentItem = index
    }

    fun connectTabs(tabs: TabLayout) {
        this.tabs = tabs
        indoorMapsContainer?.let {
            it.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
            tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(it))
        }
    }

    inner class IndoorMapsPagerAdapter constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int) = when (position) {
            0 -> BuildingMapFragment.newInstance(
                listOf("FLOOR 1"),
                listOf(R.drawable.dcl)
            )
            1 -> BuildingMapFragment.newInstance(
                listOf("FLOOR 1", "FLOOR 2", "FLOOR 3"),
                listOf(R.drawable.eceb_floor_1, R.drawable.eceb_floor_2, R.drawable.eceb_floor_3)
            )
            2 -> BuildingMapFragment.newInstance(
                listOf("FLOOR 1"),
                listOf(R.drawable.kenney)
            )
            else -> BuildingMapFragment.newInstance(
                listOf("BASEMENT", "FLOOR 1", "FLOOR 2"),
                listOf(R.drawable.siebel_floor_0, R.drawable.siebel_floor_1, R.drawable.siebel_floor_2)
            )
        }
        override fun getCount() = 4
    }

    companion object {
        fun newInstance() = IndoorMapsFragment()
    }
}
