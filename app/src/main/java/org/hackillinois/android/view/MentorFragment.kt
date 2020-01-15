package org.hackillinois.android.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.hackillinois.android.R
import org.hackillinois.android.model.mentor.MentorModel

class MentorFragment : Fragment() {

    private lateinit var mentorList: List<MentorModel>

    private lateinit var mentorRecycler: RecyclerView

    private lateinit var myAdapter: MentorAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_mentor, container, false)

        mentorRecycler = view.findViewById(R.id.mentor_recycler)

        val mentor1 = MentorModel("Raghav Saini", 0, "Siebel")
        val mentor2 = MentorModel("Eshia Rustagi", 1, "Siebel")
        val mentor3 = MentorModel("Patrick Feltes", 2, "ECE")

        mentorList = listOf(mentor1, mentor2, mentor3)

        setUpRecycler()

        return view
    }

    fun setUpRecycler() {
        myAdapter = MentorAdapter(mentorList)
        mentorRecycler.adapter = myAdapter
        mentorRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }
}