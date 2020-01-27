package org.hackillinois.android.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import org.hackillinois.android.App
import org.hackillinois.android.common.JWTUtilities
import org.hackillinois.android.database.entity.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import kotlin.concurrent.thread

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val jwt = JWTUtilities.readJWT(applicationContext)

        if (jwt != JWTUtilities.DEFAULT_JWT) {
            val api = App.getAPI(jwt)
            api.user().enqueue(object : Callback<User> {
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
}
