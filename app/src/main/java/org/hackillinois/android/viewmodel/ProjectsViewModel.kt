package org.hackillinois.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.hackillinois.android.database.entity.ProjectModel
import org.hackillinois.android.repository.ProjectRepository

class ProjectsViewModel : ViewModel() {
    private val projectsRepository = ProjectRepository.instance

    val dataScience = "Data Science"
    val languages = "Languages"
    val systems = "Systems"
    val webDev = "Wev Development"

    lateinit var dataSciLiveData: LiveData<List<ProjectModel>>
    lateinit var languageLiveData: LiveData<List<ProjectModel>>
    lateinit var systemsLiveData: LiveData<List<ProjectModel>>
    lateinit var webDevLiveData: LiveData<List<ProjectModel>>

    fun init() {
        dataSciLiveData = projectsRepository.fetchProjectsinCategory(dataScience)
        languageLiveData = projectsRepository.fetchProjectsinCategory(languages)
        systemsLiveData = projectsRepository.fetchProjectsinCategory(systems)
        webDevLiveData = projectsRepository.fetchProjectsinCategory(webDev)
    }
}