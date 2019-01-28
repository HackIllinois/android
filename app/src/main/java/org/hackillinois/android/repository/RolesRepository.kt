package org.hackillinois.android.repository

import android.arch.lifecycle.LiveData
import android.util.Log

import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Roles
import kotlin.concurrent.thread

class RolesRepository {
    private val rolesDao = App.getDatabase().rolesDao()

    private val minutesTillStale: Long = 2
    private val secondsInMinute: Long = 60
    private val millisInSecond: Long = 1000
    private val millisTillStale = minutesTillStale * secondsInMinute * millisInSecond

    fun fetchRoles(): LiveData<Roles> {
        refreshRoles()
        return rolesDao.getRoles()
    }

    private fun refreshRoles() {
        thread {
            // checks to see if an updated version of the qr code is in the database
            // if not, run API query and save it in DB
            val rolesExists = rolesDao.hasUpdatedRoles(System.currentTimeMillis() - millisTillStale) != null
            if (!rolesExists) {
                try {
                    val response = App.getAPI().roles.execute()
                    response?.let {
                        if (response.isSuccessful) {
                            val roles = it.body()
                            roles?.let {
                                roles.lastRefreshed = System.currentTimeMillis()
                                rolesDao.insert(roles)
                            }
                        }
                    }
                } catch (exception: Exception) {
                    Log.e("RolesRepository", exception.message)
                }
            }
        }
    }

    companion object {
        val instance = RolesRepository()
    }
}