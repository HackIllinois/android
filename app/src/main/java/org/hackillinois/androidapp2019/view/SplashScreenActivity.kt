package org.hackillinois.androidapp2019.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import org.hackillinois.androidapp2019.App
import org.hackillinois.androidapp2019.R
import org.hackillinois.androidapp2019.database.entity.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import kotlin.concurrent.thread

class SplashScreenActivity : AppCompatActivity() {

    private val defaultJWT: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val jwt = loadJWT()

        if (jwt != defaultJWT) {
            val api = App.getAPI(jwt)
            api.user.enqueue(object : Callback<User> {
                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("LoginActivity", "Failed to check is jwt is valid")
                    runOnUiThread {
                        launchLoginActivity()
                    }
                }

                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.code() == 200) {
                        thread {
                            try {
                                api.updateNotificationTopics().execute()
                            } catch (e: SocketTimeoutException) {
                                Log.e("LoginActivity", "Notifications update timed out!")
                            }
                        }
                        runOnUiThread {
                            launchMainActivity()
                        }
                    } else {
                        runOnUiThread {
                            launchLoginActivity()
                        }
                    }
                }
            })
        } else {
            launchLoginActivity()
        }
    }

    fun launchMainActivity() {
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    fun launchLoginActivity() {
        val mainIntent = Intent(this, LoginActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    fun loadJWT(): String {
        return applicationContext.getSharedPreferences(applicationContext.getString(R.string.authorization_pref_file_key), Context.MODE_PRIVATE).getString("jwt", defaultJWT)
                ?: defaultJWT
    }
}
