package org.hackillinois.android.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.Window.FEATURE_NO_TITLE
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.hackillinois.android.API
import org.hackillinois.android.App
import org.hackillinois.android.R
import org.hackillinois.android.common.JWTUtilities
import org.hackillinois.android.database.entity.Roles
import org.hackillinois.android.model.auth.Code
import org.hackillinois.android.model.auth.JWT
import retrofit2.HttpException
import java.net.SocketTimeoutException

class LoginActivity : AppCompatActivity() {
    private val redirectUri: String = "https://hackillinois.org/auth/?isAndroid=1"
    private val authUriTemplate: String = "https://api.hackillinois.org/auth/%s/?redirect_uri=%s"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(FEATURE_NO_TITLE)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_login)

        attendeeLoginBtn.setOnClickListener {
            redirectToOAuthProvider("github")
        }

        staffLoginBtn.setOnClickListener {
            redirectToOAuthProvider("google")
        }

        guestLoginBtn.setOnClickListener {
            launchMainActivity()
        }
    }

    override fun onResume() {
        super.onResume()

        val intent = intent ?: return
        intent.action ?: return

        val uri = intent.data ?: return

        val code = uri.getQueryParameter("code") ?: return

        finishLogin(code)
    }

    private fun finishLogin(code: String) {
        // TODO needs review
        var api = App.getAPI()
        GlobalScope.launch {
            try {
                val jwt: JWT = api.getJWT(getOAuthProvider(), redirectUri, Code(code))
                api = App.getAPI(jwt.token)
                withContext(Dispatchers.IO) {
                    try {
                        // TODO httpException 405
                        // api.updateNotificationTopics()
                    } catch (e: SocketTimeoutException) {
                        Log.e("LoginActivity", "Notifications update timed out!")
                    }
                }

                if (getOAuthProvider() == "google") {
                    verifyRole(api, jwt.token, "Staff")
                } else {
                    verifyRole(api, jwt.token, "Attendee")
                }
            } catch (e: Exception) {
                showFailedToLoginStaff()
            }
        }
    }

    private fun verifyRole(api: API, jwt: String, role: String) {
        GlobalScope.launch {
            try {
                val roles: Roles = api.roles()
                if (roles.roles.contains(role)) {
                    JWTUtilities.writeJWT(applicationContext, jwt)
                    launchMainActivity()
                } else {
                    if (role == "Staff") {
                        showFailedToLoginStaff()
                    } else {
                        showFailedToLoginAttendee()
                    }
                }
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        if (role == "Staff") {
                            showFailedToLoginStaff()
                        } else {
                            showFailedToLoginAttendee()
                        }
                    }
                    else -> {
                        showFailedToLoginStaff()
                    }
                }
            }
        }
    }

    private fun showFailedToLoginStaff() {
        Snackbar.make(
            findViewById(android.R.id.content),
            "You must have a valid staff account" +
                " to log in.",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun showFailedToLoginAttendee() {
        Snackbar.make(findViewById(android.R.id.content), "You must RSVP to log in.", Snackbar.LENGTH_SHORT).show()
    }

    fun launchMainActivity() {
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    private fun redirectToOAuthProvider(provider: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(authUriTemplate.format(provider, redirectUri))
        setOAuthProvider(provider)
        startActivity(intent)
    }

    private fun setOAuthProvider(provider: String) {
        val editor = applicationContext.getSharedPreferences(applicationContext.getString(R.string.authorization_pref_file_key), Context.MODE_PRIVATE).edit()
        editor.putString("provider", provider)
        editor.apply()
    }

    fun getOAuthProvider(): String {
        return applicationContext.getSharedPreferences(applicationContext.getString(R.string.authorization_pref_file_key), Context.MODE_PRIVATE).getString("provider", "")
            ?: ""
    }
}
