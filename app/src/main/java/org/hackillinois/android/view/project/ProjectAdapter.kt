package org.hackillinois.android.view.project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.project_list_item.view.*
import org.hackillinois.android.R
import org.hackillinois.android.common.FavoritesManager
import org.hackillinois.android.database.entity.Project

class ProjectAdapter(
    private val projectList: List<Project>,
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
            mentorName.text = project.getMentorsString()
            mentorNumber.text = "#${project.number}"
            mentorLocation.text = project.room
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
        }

        holder.itemView.setOnClickListener {
            projectClickListener.onClick(project.id)
        }
    }

    override fun getItemCount() = projectList.size
}
