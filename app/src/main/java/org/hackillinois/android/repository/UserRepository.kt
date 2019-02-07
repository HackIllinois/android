package org.hackillinois.android.repository

import android.arch.lifecycle.LiveData
import android.util.Log
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.User
import kotlin.concurrent.thread

class UserRepository {
    private val userDao = App.getDatabase().userDao()

    fun fetchUser(): LiveData<User> {
        refreshUser()
        return userDao.getUser()
    }

    private fun refreshUser() {
        thread {
            try {
                val response = App.getAPI().user.execute()
                response?.let {
                    if (response.isSuccessful) {
                        val user = it.body()
                        user?.let {
                            userDao.insert(user)
                        }
                    }
                }
            } catch (exception: Exception) {
                Log.e("UserRepository", exception.message)
            }

        }
    }

    companion object {
        val instance = UserRepository()
    }
}