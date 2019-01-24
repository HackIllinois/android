package org.hackillinois.android.view

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        attendeeLogin = findViewById(R.id.attendeeLoginBtn)
        staffLogin = findViewById(R.id.staffLoginBtn)

        attendeeLogin.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse("https://api.hackillinois.org/auth/github/?redirect_uri=https://hackillinois.org/auth/?isAndroid=1"))
            startActivity(intent)
        }

        staffLogin.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse("https://api.hackillinois.org/auth/google/?redirect_uri=https://hackillinois.org/auth/?isAndroid=1"))
            startActivity(intent)
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
                api.getJWT("github", "https://hackillinois.org/auth/?isAndroid=1", Code(code)).enqueue(object: Callback<JWT> {
                    override fun onFailure(call: Call<JWT>, t: Throwable) {
                        Log.e("LoginActivity", "Failed to get JWT")
                    }
                    override fun onResponse(call: Call<JWT>, response: Response<JWT>) {
                        Log.e("LoginActivity", response.body()?.token)
                        api = getAPI(response.body()?.token)
                        runOnUiThread {
                            launchMainActivity()
                        }
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
}