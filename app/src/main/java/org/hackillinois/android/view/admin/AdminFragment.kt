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
import kotlinx.android.synthetic.main.fragment_admin.*
import kotlinx.android.synthetic.main.fragment_admin.view.*
import org.hackillinois.android.R

class AdminFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_admin, container, false)

        val sectionsPagerAdapter = AdminFragment().SectionsPagerAdapter(childFragmentManager)

        view.adminContainer.adapter = sectionsPagerAdapter
        view.adminContainer.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(view.tabs))
        view.tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(view.adminContainer))

        return view
    }

    inner class SectionsPagerAdapter constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return ToolsFragment.newInstance(position)
        }

        override fun getCount() = 3
    }
}