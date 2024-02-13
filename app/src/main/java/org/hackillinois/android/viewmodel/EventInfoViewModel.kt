package org.hackillinois.android.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.hackillinois.android.API
import org.hackillinois.android.App
import org.hackillinois.android.R
import org.hackillinois.android.common.FavoritesManager
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.database.entity.Roles
import org.hackillinois.android.model.scanner.EventId
import org.hackillinois.android.model.user.FavoritesResponse
import org.hackillinois.android.repository.EventRepository
import org.hackillinois.android.repository.rolesRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventInfoViewModel(val app: Application) : AndroidViewModel(app) {

    private val eventRepository = EventRepository.instance
    lateinit var event: LiveData<Event>

    lateinit var roles: LiveData<Roles>

    val isFavorited = MutableLiveData<Boolean>()

    fun init(id: String) {
        viewModelScope.launch {
            event = eventRepository.fetchEvent(id)
            roles = rolesRepository.fetch()

            val favorited = FavoritesManager.isFavoritedEvent(app.applicationContext, id)
            isFavorited.postValue(favorited)
        }
    }

    fun changeFavoritedState() {
        var favorited = isFavorited.value ?: false
        favorited = !favorited

        isFavorited.postValue(favorited)

        val api: API = App.getAPI()
        val call: Call<FavoritesResponse>
        if (favorited) {
            FavoritesManager.favoriteEvent(app.applicationContext, event.value)
            call = api.followEvent(EventId(event.value!!.eventId))

            // show notification toast
            val toast = Toast.makeText(app.applicationContext, R.string.schedule_snackbar_notifications_on, Toast.LENGTH_SHORT)
            toast.show()
        } else {
            FavoritesManager.unfavoriteEvent(app.applicationContext, event.value)
            call = api.unfollowEvent(EventId(event.value!!.eventId))
        }
        call.enqueue(object : Callback<FavoritesResponse> {
            override fun onResponse(call: Call<FavoritesResponse>, response: Response<FavoritesResponse>) {
                if (response.isSuccessful) {
                    val responseData: FavoritesResponse? = response.body()
                }
            }

            override fun onFailure(call: Call<FavoritesResponse>, t: Throwable) {
                Log.d("FOLLOW/UNFOLLOW FAILURE", t.toString())
            }
        })
    }
}
