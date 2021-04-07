package org.hackillinois.android.viewmodel

import android.util.Log
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.android.synthetic.main.layout_event_code_dialog.*
import kotlinx.coroutines.launch
import org.hackillinois.android.App
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Attendee
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.database.entity.QR
import org.hackillinois.android.database.entity.User
import org.hackillinois.android.repository.attendeeRepository
import org.hackillinois.android.repository.qrRepository
import org.hackillinois.android.repository.userRepository
import org.json.JSONObject
import retrofit2.HttpException

class MainViewModel : ViewModel() {
    lateinit var attendee: LiveData<Attendee>
    lateinit var user: LiveData<User>

    fun init() {
        attendee = attendeeRepository.fetch()
        user = userRepository.fetch()
    }

    fun checkInEvent(token: String) {
        Log.d("send event token", token)
        viewModelScope.launch {
            try {
                val code: String = enterCodeField.text.toString()
                val response = App.getAPI().eventCodeCheckIn(code)
                Log.d("RESPONSE", response)
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        val error = JSONObject(e.response()?.errorBody()?.string())
                        val errorType = error.getString("type")
                        if (errorType == "ATTRIBUTE_MISMATCH_ERROR") {
                            error.getString("message")
                        } else {
                            "Internal API error"
                        }
                    }
                }
            }
        }
    }
}
