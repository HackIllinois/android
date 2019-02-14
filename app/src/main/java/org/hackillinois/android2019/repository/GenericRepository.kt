package org.hackillinois.android2019.repository

import android.arch.lifecycle.LiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

class GenericRepository<T>(
        private val apiCall: Call<T>,
        private val databaseInsertFunction: (T) -> Unit,
        private val databaseRetrievalFunction: () -> LiveData<T>
) {

    fun fetch(): LiveData<T> {
        refresh()
        return databaseRetrievalFunction()
    }

    private fun refresh() {
        apiCall.clone().enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    thread {
                        response.body()?.let {
                            databaseInsertFunction(it)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {}
        })
    }

}