package org.hackillinois.android.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import org.hackillinois.android.R
import org.hackillinois.android.model.mentor.MentorModel

class MentorFragment : Fragment() {

    private lateinit var mentorList: ArrayList<MentorModel>

    private lateinit var mentorRecycler: RecyclerView

    private lateinit var myAdapter: MentorAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_mentor, container, false)

        return view
    }

    fun setUpRecycler() {
    }
}