package org.hackillinois.android.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import org.hackillinois.android.App.getAPI
import org.hackillinois.android.R
import org.hackillinois.android.model.Code
import org.hackillinois.android.model.JWT
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private val redirectUri: String = "https://hackillinois.org/auth/?isAndroid=1"
    private val authUriTemplate: String = "https://api.hackillinois.org/auth/%s/?redirect_uri=%s"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        attendeeLoginBtn.setOnClickListener {
            redirectToOAuthProvider("github")
        }

        staffLoginBtn.setOnClickListener {
            redirectToOAuthProvider("google")
        }

        recruiterLoginBtn.setOnClickListener {
            redirectToOAuthProvider("linkedin")
        }
    }

    override fun onResume() {
        super.onResume()

        val intent = getIntent()

        intent?: return
        intent.action?: return

        val uri = intent.data

        uri?: return

        val code = uri.getQueryParameter("code")
        var api = getAPI()

        api.getJWT(getOAuthProvider(), redirectUri, Code(code)).enqueue(object: Callback<JWT> {
            override fun onFailure(call: Call<JWT>, t: Throwable) {
                Toast.makeText(applicationContext, "Failed to login", Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<JWT>, response: Response<JWT>) {
                response.body()?.token?.let {
                    api = getAPI(it)
                    storeJWT(it)
                    runOnUiThread {
                        launchMainActivity()
                    }
                }
            }
        })
    }

    fun launchMainActivity() {
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    fun redirectToOAuthProvider(provider: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(Uri.parse(authUriTemplate.format(provider, redirectUri)))
        setOAuthProvider(provider)
        startActivity(intent)
    }

    fun setOAuthProvider(provider: String) {
        val editor = applicationContext.getSharedPreferences(applicationContext.getString(R.string.authorization_pref_file_key), Context.MODE_PRIVATE).edit()
        editor.putString("provider", provider)
        editor.apply()
    }

    fun getOAuthProvider(): String {
        return applicationContext.getSharedPreferences(applicationContext.getString(R.string.authorization_pref_file_key), Context.MODE_PRIVATE).getString("provider", "")?: ""
    }

    fun storeJWT(jwt: String) {
        val editor = applicationContext.getSharedPreferences(applicationContext.getString(R.string.authorization_pref_file_key), Context.MODE_PRIVATE).edit()
        editor.putString("jwt", jwt)
        editor.apply()
    }
}