package org.hackillinois.android.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import org.hackillinois.android.App.getAPI
import org.hackillinois.android.R
import org.hackillinois.android.model.Code
import org.hackillinois.android.model.JWT
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var attendeeLogin: Button
    private lateinit var staffLogin: Button
    private lateinit var recruiterLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        attendeeLogin = findViewById(R.id.attendeeLoginBtn)
        staffLogin = findViewById(R.id.staffLoginBtn)
        recruiterLogin = findViewById(R.id.recruiterLoginBtn)

        attendeeLogin.setOnClickListener {
            redirectToOAuthProvider("github")
        }

        staffLogin.setOnClickListener {
            redirectToOAuthProvider("google")
        }

        recruiterLogin.setOnClickListener {
            redirectToOAuthProvider("linkedin")
        }

        var jwt = loadJWT();

        if(jwt != "") {
            getAPI(jwt)
            launchMainActivity()
        }
    }

    override fun onResume() {
        super.onResume()

        var intent = getIntent()

        if(intent != null && intent.action != null) {
            var uri = intent.data

            if(uri != null) {
                var code = uri.getQueryParameter("code")
                Log.e("LoginActivity", code)
                var api = getAPI()
                api.getJWT(getOAuthProvider(), "https://hackillinois.org/auth/?isAndroid=1", Code(code)).enqueue(object: Callback<JWT> {
                    override fun onFailure(call: Call<JWT>, t: Throwable) {
                        Log.e("LoginActivity", "Failed to get JWT")
                    }
                    override fun onResponse(call: Call<JWT>, response: Response<JWT>) {
                        response.body()?.token?.let {
                            Log.e("LoginActivity", it)
                            api = getAPI(it)
                            storeJWT(it)
                            runOnUiThread {
                                launchMainActivity()
                            }
                        }
                        Log.e("LoginActivity", "Error logging in")
                    }
                })
            }
        }
    }

    fun launchMainActivity() {
        var mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    fun redirectToOAuthProvider(provider: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(Uri.parse("https://api.hackillinois.org/auth/$provider/?redirect_uri=https://hackillinois.org/auth/?isAndroid=1"))
        setOAuthProvider(provider)
        startActivity(intent)
    }

    fun setOAuthProvider(provider: String) {
        var editor = applicationContext.getSharedPreferences(applicationContext.getString(R.string.authorization_pref_file_key), Context.MODE_PRIVATE).edit()
        editor.putString("provider", provider)
        editor.apply()
    }

    fun getOAuthProvider(): String {
        applicationContext.getSharedPreferences(applicationContext.getString(R.string.authorization_pref_file_key), Context.MODE_PRIVATE).getString("provider", "")?.let {
            return it
        }
        return ""
    }

    fun storeJWT(jwt: String) {
        var editor = applicationContext.getSharedPreferences(applicationContext.getString(R.string.authorization_pref_file_key), Context.MODE_PRIVATE).edit()
        editor.putString("jwt", jwt)
        editor.apply()
    }

    fun loadJWT(): String {
        applicationContext.getSharedPreferences(applicationContext.getString(R.string.authorization_pref_file_key), Context.MODE_PRIVATE).getString("provider", "")?.let {
            return it
        }
        return ""
    }
}