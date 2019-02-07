package org.hackillinois.android.repository

import android.arch.lifecycle.LiveData
import android.util.Log

import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Roles
import kotlin.concurrent.thread

class RolesRepository {
    private val rolesDao = App.getDatabase().rolesDao()

    fun fetchRoles(): LiveData<Roles> {
        refreshRoles()
        return rolesDao.getRoles()
    }

    private fun refreshRoles() {
        thread {
            try {
                val response = App.getAPI().roles.execute()
                response?.let {
                    if (response.isSuccessful) {
                        val roles = it.body()
                        roles?.let {
                            rolesDao.insert(roles)
                        }
                    }
                }
            } catch (exception: Exception) {
                Log.e("RolesRepository", exception.message)
            }
        }
    }

    companion object {
        val instance = RolesRepository()
    }
}