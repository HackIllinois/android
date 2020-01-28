package org.hackillinois.android.view.project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.project_list_item.view.*
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Project

class ProjectAdapter internal constructor(private val projectList: List<Project>) : RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {

    private lateinit var context: Context

    inner class ViewHolder(parent: View) : RecyclerView.ViewHolder(parent)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.project_list_item, parent, false)
        val viewHolder = ViewHolder(view)
        context = parent.context
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = projectList[position]

        holder.itemView.apply {
            mentorName.text = project.getMentorsString()
            mentorNumber.text = "#${project.number}"
            mentorLocation.text = project.room
        }
    }

    override fun getItemCount() = projectList.size
}