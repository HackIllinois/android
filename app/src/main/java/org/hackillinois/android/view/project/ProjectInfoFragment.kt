package org.hackillinois.android.view.project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.hackillinois.android.R

class ProjectInfoFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_project_info, container, false)
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
