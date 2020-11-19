package org.hackillinois.android.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Project
import org.hackillinois.android.model.projects.ProjectsList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import kotlin.concurrent.thread

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
            } catch(e: Exception) {}
        }
    }

    companion object {
        val instance: ProjectRepository by lazy { ProjectRepository() }
    }
}
