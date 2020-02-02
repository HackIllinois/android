package org.hackillinois.android.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.hackillinois.android.common.FavoritesManager
import org.hackillinois.android.database.entity.Project
import org.hackillinois.android.repository.ProjectRepository

class ProjectInfoViewModel(val app: Application) : AndroidViewModel(app) {

    private val projectRepository = ProjectRepository.instance
    lateinit var project: LiveData<Project>

    val projectFavorited = MutableLiveData<Boolean>()

    fun init(projectId: String) {
        project = projectRepository.fetchProject(projectId)
    }

    fun changeFavoritedState() {
        var favorited = projectFavorited.value ?: false
        favorited = !favorited

        projectFavorited.postValue(favorited)

        if (favorited) {
            FavoritesManager.favoriteProject(app.applicationContext, project.value)
        } else {
            FavoritesManager.unfavoriteProject(app.applicationContext, project.value)
        }
    }
}
