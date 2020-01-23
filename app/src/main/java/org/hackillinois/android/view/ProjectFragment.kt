package org.hackillinois.android.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.ProjectModel

class ProjectFragment : Fragment() {

    private lateinit var mentorList: List<ProjectModel>

    private lateinit var mentorRecycler: RecyclerView

    private lateinit var myAdapter: ProjectAdapter

    private val projects: String = "https://api.hackillinois.org/project/"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_project, container, false)

        mentorRecycler = view.findViewById(R.id.mentor_recycler)

        val mentor1 = ProjectModel("Raghav Saini", 0, "Siebel")
        val mentor2 = ProjectModel("Eshia Rustagi", 1, "Siebel")
        val mentor3 = ProjectModel("Patrick Feltes", 2, "ECE")

        mentorList = listOf(mentor1, mentor2, mentor3)

        setUpRecycler()

        return view
    }

    fun setUpRecycler() {
        myAdapter = ProjectAdapter(mentorList)
        mentorRecycler.adapter = myAdapter
        mentorRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }
}