package org.hackillinois.android.repository

import android.arch.lifecycle.LiveData
import android.util.Log
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.User
import kotlin.concurrent.thread

class UserRepository {
    private val userDao = App.getDatabase().userDao()

    private val minutesTillStale: Long = 2
    private val secondsInMinute: Long = 60
    private val millisInSecond: Long = 1000
    private val millisTillStale = minutesTillStale * secondsInMinute * millisInSecond

    fun fetchUser(): LiveData<User> {
        refreshUser()
        return userDao.getUser()
    }

    private fun refreshUser() {
        thread {
            // checks to see if an updated version of the qr code is in the database
            // if not, run API query and save it in DB
            val userExists = userDao.hasUpdatedUser(System.currentTimeMillis() - millisTillStale) != null
            if (!userExists) {
                try {
                    val response = App.getAPI().user.execute()
                    response?.let {
                        if (response.isSuccessful) {
                            val user = it.body()
                            user?.let {
                                user.lastRefreshed = System.currentTimeMillis()
                                userDao.insert(user)
                            }
                        }
                    }
                } catch (exception: Exception) {
                    Log.e("UserRepository", exception.message)
                }

            }
        }
    }

    companion object {
        val instance = UserRepository()
    }
}