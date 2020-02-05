package org.hackillinois.android.view.project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_project.view.*
import kotlinx.android.synthetic.main.fragment_project.view.projectCategories
import org.hackillinois.android.R
import org.hackillinois.android.viewmodel.ProjectsViewModel

class ProjectFragment : Fragment() {

    private lateinit var favoriteProjectButton: Button
    private lateinit var projectsViewModel: ProjectsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        projectsViewModel = ViewModelProviders.of(this).get(ProjectsViewModel::class.java)
        projectsViewModel.showFavorites.observe(this, Observer {
            favoriteProjectButton.isSelected = it ?: false
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_project, container, false)

        val mentorPageAdapter = MentorPageAdapter(childFragmentManager)

        view.projectsContainer.adapter = mentorPageAdapter
        view.projectsContainer.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(view.projectCategories))
        view.projectCategories.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(view.projectsContainer))

        favoriteProjectButton = view.findViewById(R.id.favorite_project_button)

        favoriteProjectButton.setOnClickListener(favScheduleClickListener)

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

    private val favScheduleClickListener = View.OnClickListener {
        favoriteProjectButton.isSelected = !favoriteProjectButton.isSelected
        projectsViewModel.showFavorites.postValue(favoriteProjectButton.isSelected)
    }
}