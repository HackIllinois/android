package org.hackillinois.android.repository

import androidx.lifecycle.LiveData
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.ProjectModel
import org.hackillinois.android.model.projects.ProjectsList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

class ProjectRepository {
    private val projectsDao = App.database.projectsDao()

    fun fetchProjectsinCategory(category: String): LiveData<List<ProjectModel>> {
        refreshAll()
        return projectsDao.getProjectsWithTag(category)
    }

    private fun refreshAll() {
        App.getAPI().allProjects().enqueue(object : Callback<ProjectsList> {
            override fun onResponse(call: Call<ProjectsList>, response: Response<ProjectsList>) {
                if (response.isSuccessful) {
                    val projectsList: List<ProjectModel> = response.body()?.projects ?: return
                    thread { projectsDao.clearTableAndInsertProjects(projectsList) }
                }
            }

            override fun onFailure(call: Call<ProjectsList>, t: Throwable) {}
        })
    }
}
