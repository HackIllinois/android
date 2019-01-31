package org.hackillinois.android.viewmodel

import android.R
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import okhttp3.ResponseBody
import org.hackillinois.android.App
import org.hackillinois.android.model.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminViewModel : ViewModel() {
    var stats = MutableLiveData<String>()
    var eventCreated = MutableLiveData<Boolean>()
    var notificationTopics = MutableLiveData<NotificationTopics>()
    var notificationCreated = MutableLiveData<Boolean>()

    fun queryStats() {
        App.getAPI().stats.enqueue(object: Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                stats.postValue(null)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                response.body()?.let {
                    val data = JSONObject(it.string()).toString(4)
                    stats.postValue(data)
                }
            }
        })
    }

    fun createEvent(name: String, description: String, sponsor: String, eventType: String,
                    eventRoom: String, startTime: Long, endTime: Long, locationName: String) {
        var location: EventLocation? = null
        when(locationName) {
            "Siebel Center" -> location = EventLocation("${SiebelCenter.description} $eventRoom", SiebelCenter.latitude, SiebelCenter.longitude)
            "ECE Building" -> location = EventLocation("${EceBuilding.description} $eventRoom", EceBuilding.latitude, EceBuilding.longitude)
        }

        location?.let {
            val event = Event(name, description, startTime, endTime, listOf(location), sponsor, eventType)
            App.getAPI().createEvent(event).enqueue(object: Callback<Event> {
                override fun onFailure(call: Call<Event>, t: Throwable) {
                    eventCreated.postValue(false)
                }

                override fun onResponse(call: Call<Event>, response: Response<Event>) {
                    eventCreated.postValue(response.code() == 200)
                }
            })
        }
    }

    fun getNotificationTopics() {
        App.getAPI().notificationTopics.enqueue(object: Callback<NotificationTopics> {
            override fun onFailure(call: Call<NotificationTopics>, t: Throwable) {
                notificationTopics.postValue(null)
            }

            override fun onResponse(call: Call<NotificationTopics>, response: Response<NotificationTopics>) {
                response.body()?.let {
                    notificationTopics.postValue(it)
                }
            }
        })
    }

    fun createNotification(topic: String, title: String, body: String) {
        val notification = Notification(title, body)

        App.getAPI().createNotification(topic, notification).enqueue(object: Callback<Notification> {
            override fun onFailure(call: Call<Notification>, t: Throwable) {
                notificationCreated.postValue(false)
            }

            override fun onResponse(call: Call<Notification>, response: Response<Notification>) {
                notificationCreated.postValue(response.code() == 200)
            }

        })
    }
}