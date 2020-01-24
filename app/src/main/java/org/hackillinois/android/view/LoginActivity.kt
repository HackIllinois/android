package org.hackillinois.android.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_login.*
import org.hackillinois.android.App
import org.hackillinois.android.R
import org.hackillinois.android.common.JWTUtilities
import org.hackillinois.android.model.auth.Code
import org.hackillinois.android.model.auth.JWT
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import kotlin.concurrent.thread

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

        intent ?: return
        intent.action ?: return

        val uri = intent.data

        uri ?: return

        val code = uri.getQueryParameter("code")
        var api = App.getAPI()

        api.getJWT(getOAuthProvider(), redirectUri, Code(code)).enqueue(object : Callback<JWT> {
            override fun onFailure(call: Call<JWT>, t: Throwable) {
                Snackbar.make(findViewById(android.R.id.content), "Failed to login", Snackbar.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<JWT>, response: Response<JWT>) {
                response.body()?.token?.let {
                    api = App.getAPI(it)
                    thread {
                        try {
                            api.updateNotificationTopics().execute()
                        } catch (e: SocketTimeoutException) {
                            Log.e("LoginActivity", "Notifications update timed out!")
                        }
                    }
                    JWTUtilities.writeJWT(applicationContext, it)
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
        return applicationContext.getSharedPreferences(applicationContext.getString(R.string.authorization_pref_file_key), Context.MODE_PRIVATE).getString("provider", "")
                ?: ""
    }
}