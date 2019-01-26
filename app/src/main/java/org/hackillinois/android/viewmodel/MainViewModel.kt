package org.hackillinois.android.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import org.hackillinois.android.database.entity.User
import org.hackillinois.android.repository.UserRepository

class MainViewModel : ViewModel() {

    private val userRepository = UserRepository.instance
    lateinit var user: LiveData<User>

    fun init() {
        user = userRepository.fetchUser()
    }
}
