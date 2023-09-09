package org.hackillinois.android.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import org.hackillinois.android.model.auth.JWT
import java.net.SocketTimeoutException

class LoginActivity : AppCompatActivity() {
    private val authUriTemplate: String = "https://adonix.hackillinois.org/auth/login/%s/?device=android"

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

    private fun redirectToOAuthProvider(provider: String) {
        setOAuthProvider(provider)

        // create Intent to go to OAuth page (either google or github) using the
        // the api query string found at the top of this class
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(authUriTemplate.format(provider))
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()

        // if returning from intent, get the uri query parameter from api: token={...}
        val intent = intent ?: return
        intent.action ?: return
        val uri = intent.data ?: return
        val token = uri.getQueryParameter("token") ?: return

        // finish logging in using the received token
        finishLogin(token)
    }

    private fun finishLogin(token: String) {
        GlobalScope.launch {
            try {
                val jwt = JWT(token)
                val api = App.getAPI(token)
                withContext(Dispatchers.IO) {
                    try {
                        // TODO httpException 405
                        // api.updateNotificationTopics()
                    } catch (e: SocketTimeoutException) {
                        Log.e("LoginActivity", "Notifications update timed out!")
                    }
                }

                // verify user's roles are correct
                if (getOAuthProvider() == "google") {
                    verifyRole(api, jwt.token, "Staff")
                } else {
                    verifyRole(api, jwt.token, "Attendee")
                }
            } catch (e: Exception) {
                showFailedToLogin(e.message)
            }
        }
    }

    private fun verifyRole(api: API, jwt: String, role: String) {
        GlobalScope.launch {
            try {
                val loginRoles: Roles = api.roles()
                // Check if user's roles are correct. If not, display corresponding error message
                if (loginRoles.roles.contains(role)) {
                    JWTUtilities.writeJWT(applicationContext, jwt) // save JWT to sharedPreferences for auto-login in the future
                    launchMainActivity()
                } else {
                    if (role == "Staff") {
                        showFailedToLoginStaff()
                    } else {
                        showFailedToLoginAttendee()
                    }
                }
            } catch (e: Exception) {
                showFailedToLogin(e.message)
            }
        }
    }

    private fun showFailedToLogin(message: String?) {
        if (message != null) {
            Snackbar.make(
                findViewById(android.R.id.content),
                message.toString(),
                Snackbar.LENGTH_SHORT,
            ).show()
        } else {
            Snackbar.make(
                findViewById(android.R.id.content),
                "Failed to login. Please try again.",
                Snackbar.LENGTH_SHORT,
            ).show()
        }
    }

    private fun showFailedToLoginStaff() {
        Snackbar.make(
            findViewById(android.R.id.content),
            "You must have a valid staff account to log in.",
            Snackbar.LENGTH_SHORT,
        ).show()
    }

    private fun showFailedToLoginAttendee() {
        Snackbar.make(
            findViewById(android.R.id.content),
            "You must RSVP to log in.",
            Snackbar.LENGTH_SHORT,
        ).show()
    }

    private fun launchMainActivity() {
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    private fun setOAuthProvider(provider: String) {
        val editor = applicationContext.getSharedPreferences(applicationContext.getString(R.string.authorization_pref_file_key), Context.MODE_PRIVATE).edit()
        editor.putString("provider", provider)
        editor.apply()
    }

    private fun getOAuthProvider(): String {
        return applicationContext.getSharedPreferences(applicationContext.getString(R.string.authorization_pref_file_key), Context.MODE_PRIVATE).getString("provider", "")
            ?: ""
    }
}
