package org.hackillinois.android.repository

import android.arch.lifecycle.LiveData
import android.util.Log

import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Roles
import org.hackillinois.android.database.entity.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

class RolesRepository {
    private val rolesDao = App.getDatabase().rolesDao()

    fun fetchRoles(): LiveData<Roles> {
        refreshRoles()
        return rolesDao.getRoles()
    }

    private fun refreshRoles() {
        App.getAPI().roles.enqueue(object : Callback<Roles> {
            override fun onResponse(call: Call<Roles>, response: Response<Roles>) {
                if (response.isSuccessful) {
                    val roles = response.body()
                    thread {
                        roles?.let { rolesDao.insert(it) }
                    }
                }
            }

            override fun onFailure(call: Call<Roles>, t: Throwable) { }
        })
    }

    companion object {
        val instance = RolesRepository()
    }
}