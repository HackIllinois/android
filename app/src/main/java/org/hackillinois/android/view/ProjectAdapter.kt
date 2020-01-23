package org.hackillinois.android.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.mentor_list_item.view.*
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.ProjectModel

class ProjectAdapter internal constructor(private val mentorList: List<ProjectModel>) : RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {

    private lateinit var context: Context

    inner class ViewHolder(parent: View) : RecyclerView.ViewHolder(parent)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mentor_list_item, parent, false)
        val viewHolder = ViewHolder(view)
        context = parent.context
        return viewHolder
    }

    override fun onBindViewHolder(holder: ProjectAdapter.ViewHolder, position: Int) {
        val mentor = mentorList[position]

        holder.itemView.apply {
            mentorName.text = mentor.name
            mentorNumber.text = "#${mentor.number}"
            mentorLocation.text = mentor.location
        }
    }

    override fun getItemCount() = mentorList.size
}