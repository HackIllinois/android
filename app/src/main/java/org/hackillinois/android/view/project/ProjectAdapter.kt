package org.hackillinois.android.view.project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.project_list_item.view.*
import kotlinx.android.synthetic.main.project_tags.view.*
import org.hackillinois.android.R
import org.hackillinois.android.common.FavoritesManager
import org.hackillinois.android.database.entity.Project

class ProjectAdapter(
    private var projectList: List<Project>,
    private val projectClickListener: ProjectClickListener
) : RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {

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
            project_name.text = project.name
            table_number.text = "Table #${project.number}"
            project_meeting_room.text = "Meeting Room: ${project.room}"
            setOnClickListener { projectClickListener.onClick(project.id) }
            favoriteProject.isSelected = FavoritesManager.isFavoritedProject(context, project.id)

            favoriteProject.setOnClickListener { button ->
                button.isSelected = !button.isSelected

                if (button.isSelected) {
                    FavoritesManager.favoriteProject(context, project)
                    Snackbar.make(button, R.string.project_favorited_notif, Snackbar.LENGTH_SHORT).show()
                } else {
                    FavoritesManager.unfavoriteProject(context, project)
                }
            }
            project.tags.let {
                data_sci_tag.visibility = if (it.contains("Data Science")) View.VISIBLE else View.GONE
                web_dev_tag.visibility = if (it.contains("Web Development")) View.VISIBLE else View.GONE
                languages_tag.visibility = if (it.contains("Languages")) View.VISIBLE else View.GONE
                systems_tag.visibility = if (it.contains("Systems")) View.VISIBLE else View.GONE
            }
        }
    }

    override fun getItemCount() = projectList.size

    fun updateProjects(list: List<Project>) {
        this.projectList = list
        notifyDataSetChanged()
    }
}
