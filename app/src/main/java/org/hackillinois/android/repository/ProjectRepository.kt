package org.hackillinois.android.repository

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Project
import java.lang.Exception

class ProjectRepository {
    private val projectsDao = App.database.projectsDao()

    fun fetchProjectsinCategory(category: String): LiveData<List<Project>> {
        refreshAll()
        return projectsDao.getProjectsWithTag(category)
    }

    fun fetchProject(projectId: String): LiveData<Project> {
        refreshAll()
        return projectsDao.getProject(projectId)
    }

    private fun refreshAll() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val projectsList = App.getAPI().allProjects()
                projectsDao.clearTableAndInsertProjects(projectsList.projects)
            } catch (e: Exception) {
                Log.e("Projectrepo refreshAll", e.toString())
            }
        }
    }

    companion object {
        val instance: ProjectRepository by lazy { ProjectRepository() }
    }
}
