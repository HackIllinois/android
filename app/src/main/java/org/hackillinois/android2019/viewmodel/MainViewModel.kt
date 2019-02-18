package org.hackillinois.android2019.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import org.hackillinois.android2019.database.entity.Roles
import org.hackillinois.android2019.database.entity.User
import org.hackillinois.android2019.repository.rolesRepository
import org.hackillinois.android2019.repository.userRepository

class MainViewModel : ViewModel() {

    lateinit var user: LiveData<User>
    lateinit var roles: LiveData<Roles>

    fun init() {
        user = userRepository.fetch()
        roles = rolesRepository.fetch()
    }

    fun refresh() {
        user = userRepository.fetch()
        roles = rolesRepository.fetch()
    }
}
