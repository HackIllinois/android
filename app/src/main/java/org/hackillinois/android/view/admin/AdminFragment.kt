package org.hackillinois.android.view.admin

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.hackillinois.android.R

class AdminFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_admin, container, false)

        val viewPager = view.findViewById<ViewPager>(R.id.container)
        val tabLayout = view.findViewById<TabLayout>(R.id.tabs)
        val sectionsPagerAdapter = AdminFragment().SectionsPagerAdapter(childFragmentManager)

        viewPager.adapter = sectionsPagerAdapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))

        return view
    }

    inner class SectionsPagerAdapter constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return ToolsFragment.newInstance(position)
        }

        override fun getCount(): Int {
            return 3
        }
    }
}