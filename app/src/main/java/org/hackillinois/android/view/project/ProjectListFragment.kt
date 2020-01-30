package org.hackillinois.android.view.project

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Project
import org.hackillinois.android.view.MainActivity
import org.hackillinois.android.viewmodel.ProjectsViewModel

class ProjectListFragment : Fragment(), ProjectClickListener {

    private var projectList: List<Project>? = null
    private lateinit var projectsRecycler: RecyclerView
    private var myAdapter: ProjectAdapter? = null
    private var listState: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tabName = arguments?.getString(ARG_TAB_NUM) ?: "Data Science"

        val viewModel = ViewModelProviders.of(this).get(ProjectsViewModel::class.java)
        viewModel.init()

        val liveData = when (tabName) {
            viewModel.dataScience -> viewModel.dataSciLiveData
            viewModel.languages -> viewModel.languageLiveData
            viewModel.systems -> viewModel.systemsLiveData
            else -> viewModel.webDevLiveData
        }

        liveData.observe(this, Observer { projects -> projects?.let {
            projectList = projects
            myAdapter = ProjectAdapter(projects, this)
            projectsRecycler.adapter = myAdapter
        } })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_project_list, container, false)

        projectsRecycler = view.findViewById(R.id.project_recycler)
        projectsRecycler.layoutManager = LinearLayoutManager(context)

        projectList?.let {
            myAdapter = ProjectAdapter(it, this)
            projectsRecycler.adapter = myAdapter
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        if (listState != null) {
            projectsRecycler.layoutManager?.onRestoreInstanceState(listState)
        }
    }

    override fun onPause() {
        super.onPause()
        listState = projectsRecycler.layoutManager?.onSaveInstanceState()
    }

    override fun onResume() {
        super.onResume()
        myAdapter?.notifyDataSetChanged()
    }

    override fun onClick(projectId: String) {
        val newFragment = ProjectInfoFragment.newInstance(projectId)
        (activity as MainActivity).switchFragment(newFragment, true)
    }

    companion object {
        private val ARG_TAB_NUM = "tab_string"

        fun newInstance(tagName: String): ProjectListFragment {
            val fragment = ProjectListFragment()
            val args = Bundle()

            args.putString(ARG_TAB_NUM, tagName)
            fragment.arguments = args

            return fragment
        }
    }
}
