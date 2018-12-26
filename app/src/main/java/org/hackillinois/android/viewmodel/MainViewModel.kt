package org.hackillinois.android.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Attendee
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    val attendee = MutableLiveData<Attendee>()

    fun getNameAndEmail() {
        App.getAPI().attendee.enqueue(object : Callback<Attendee> {
            override fun onFailure(call: Call<Attendee>, t: Throwable) {
                Log.w("MainViewModel", "Failed to fetch attendee data")
            }

            override fun onResponse(call: Call<Attendee>, response: Response<Attendee>) {
                attendee.postValue(response.body())
            }
        })
    }
}
