package org.hackillinois.android.view.project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.davemorrissey.labs.subscaleview.ImageSource
import kotlinx.android.synthetic.main.fragment_project_info.*
import kotlinx.android.synthetic.main.fragment_project_info.view.*
import kotlinx.android.synthetic.main.project_tags.*
import org.hackillinois.android.R
import org.hackillinois.android.common.DirectionsOnClickListener
import org.hackillinois.android.database.entity.Project
import org.hackillinois.android.viewmodel.ProjectInfoViewModel

class ProjectInfoFragment : Fragment() {

    private lateinit var viewModel: ProjectInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val projectId = arguments?.getString(PROJECT_ID_KEY) ?: ""
        viewModel = ViewModelProviders.of(this).get(ProjectInfoViewModel::class.java)
        viewModel.init(projectId)
        viewModel.project.observe(this, Observer { updateProjectUI(it) })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_project_info, container, false)
        view.closeButton.setOnClickListener { activity?.onBackPressed() }
        return view
    }

    private fun updateProjectUI(project: Project?) = project?.let {
        projectNameTextView.text = "#${it.number} ${it.name}"
        mentorNamesTextView.text = it.getMentorsString()
        locationTextView.text = it.room
        descriptionTextView.text = it.description
        cardLocationTextView.text = it.room

        val indoorMapResource = it.getIndoorMapResource()
        if (indoorMapResource != 0) {
            map_image_view.setImage(ImageSource.resource(indoorMapResource))
        }

        val location = it.getLatLng()
        directionsButton.setOnClickListener(DirectionsOnClickListener(location, it.getBuildingName()))

        if (it.tags.contains("Data Science")) data_sci_tag.visibility = View.VISIBLE
        if (it.tags.contains("Web Development")) web_dev_tag.visibility = View.VISIBLE
        if (it.tags.contains("Languages")) languages_tag.visibility = View.VISIBLE
        if (it.tags.contains("Systems")) systems_tag.visibility = View.VISIBLE
    }

    companion object {
        const val PROJECT_ID_KEY = "project_id"

        fun newInstance(projectId: String): Fragment {
            val fragment = ProjectInfoFragment()

            val bundle = Bundle().apply {
                putString(PROJECT_ID_KEY, projectId)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}
