package org.hackillinois.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.hackillinois.android.database.entity.Roles
import org.hackillinois.android.database.entity.User
import org.hackillinois.android.repository.rolesRepository
import org.hackillinois.android.repository.userRepository

class MainViewModel : ViewModel() {

    lateinit var user: LiveData<User>
    lateinit var roles: LiveData<Roles>

    fun init() {
        user = userRepository.fetch()
        roles = rolesRepository.fetch()
    }
}
