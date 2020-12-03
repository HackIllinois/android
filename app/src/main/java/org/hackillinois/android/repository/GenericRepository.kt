package org.hackillinois.android.repository

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class GenericRepository<T>(
    private val apiCall: suspend () -> T,
    private val databaseInsertFunction: (T) -> Unit,
    private val databaseRetrievalFunction: () -> LiveData<T>
) {

    fun fetch(): LiveData<T> {
        refresh()
        return databaseRetrievalFunction()
    }

    private fun refresh() {
        GlobalScope.launch {
            try {
                val t: T = apiCall()
                withContext(Dispatchers.IO) {
                    databaseInsertFunction(t)
                }
            } catch (e: Exception) {
                Log.e("GenericRepo refresh", e.toString())
            }
        }
    }
}