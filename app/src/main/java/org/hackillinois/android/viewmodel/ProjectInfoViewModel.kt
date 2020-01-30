package org.hackillinois.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.hackillinois.android.database.entity.Project
import org.hackillinois.android.repository.ProjectRepository

class ProjectInfoViewModel : ViewModel() {

    private val projectRepository = ProjectRepository.instance
    lateinit var project: LiveData<Project>

    fun init(projectId: String) {
        project = projectRepository.fetchProject(projectId)
    }
}
