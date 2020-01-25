package org.hackillinois.android.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_project.*
import kotlinx.android.synthetic.main.fragment_project.view.*
import kotlinx.android.synthetic.main.fragment_project.view.projectCategories
import org.hackillinois.android.R
import org.hackillinois.android.viewmodel.ProjectsViewModel

class ProjectFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_project, container, false)

        val mentorPageAdapter = MentorPageAdapter(childFragmentManager)

        view.projectsContainer.adapter = mentorPageAdapter
        view.projectsContainer.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(view.projectCategories))
        view.projectCategories.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(view.projectsContainer))

        val projectCategories = view.findViewById<TabLayout>(R.id.projectCategories)

        view.projectsContainer.currentItem = projectCategories.selectedTabPosition

        return view
    }

    inner class MentorPageAdapter constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        val projectsViewModel = ProjectsViewModel()
        val tabNames = arrayOf(projectsViewModel.dataScience, projectsViewModel.languages,
                projectsViewModel.systems, projectsViewModel.webDev)
        override fun getItem(position: Int) = ProjectListFragment.newInstance(tabNames[position])
        override fun getCount() = 4
    }
}