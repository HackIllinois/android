package org.hackillinois.android.view

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.hackillinois.android.App

import org.hackillinois.android.R
import org.hackillinois.android.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var jwt = loadJWT();

        if(jwt != "") {
            var api = App.getAPI(jwt)
            api.user.enqueue(object: Callback<User> {
                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("LoginActivity", "Failed to check is jwt is valid")
                }

                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if(response.code() == 200) {
                        runOnUiThread {
                            launchMainActivity()
                        }
                    }
                }
            })
        } else {
            launchLoginActivity()
        }
    }

    fun launchMainActivity() {
        var mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    fun launchLoginActivity() {
        var mainIntent = Intent(this, LoginActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    fun loadJWT(): String {
        applicationContext.getSharedPreferences(applicationContext.getString(R.string.authorization_pref_file_key), Context.MODE_PRIVATE).getString("jwt", "")?.let {
            return it
        }
        return ""
    }
}
