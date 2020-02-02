package org.hackillinois.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.hackillinois.android.database.entity.Project
import org.hackillinois.android.repository.ProjectRepository

class ProjectsViewModel : ViewModel() {
    private val projectsRepository = ProjectRepository.instance

    val dataScience = "Data Science"
    val languages = "Languages"
    val systems = "Systems"
    val webDev = "Web Development"

    lateinit var dataSciLiveData: LiveData<List<Project>>
    lateinit var languageLiveData: LiveData<List<Project>>
    lateinit var systemsLiveData: LiveData<List<Project>>
    lateinit var webDevLiveData: LiveData<List<Project>>

    fun init() {
        dataSciLiveData = projectsRepository.fetchProjectsinCategory(dataScience)
        languageLiveData = projectsRepository.fetchProjectsinCategory(languages)
        systemsLiveData = projectsRepository.fetchProjectsinCategory(systems)
        webDevLiveData = projectsRepository.fetchProjectsinCategory(webDev)
    }
}
