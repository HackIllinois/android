package org.hackillinois.android.repository

import android.arch.lifecycle.LiveData
import android.util.Log
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

class UserRepository {
    private val userDao = App.getDatabase().userDao()

    fun fetchUser(): LiveData<User> {
        refreshUser()
        return userDao.getUser()
    }

    private fun refreshUser() {
        App.getAPI().user.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    thread {
                        user?.let { userDao.insert(it) }
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) { }
        })
    }

    companion object {
        val instance = UserRepository()
    }
}