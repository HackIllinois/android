package org.hackillinois.androidapp2019.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import org.hackillinois.androidapp2019.database.entity.Roles
import org.hackillinois.androidapp2019.database.entity.User
import org.hackillinois.androidapp2019.repository.rolesRepository
import org.hackillinois.androidapp2019.repository.userRepository

class MainViewModel : ViewModel() {

    lateinit var user: LiveData<User>
    lateinit var roles: LiveData<Roles>

    fun init() {
        user = userRepository.fetch()
        roles = rolesRepository.fetch()
    }
}
