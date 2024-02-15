package org.hackillinois.android.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.hackillinois.android.API
import org.hackillinois.android.App
import org.hackillinois.android.R
import org.hackillinois.android.common.FavoritesManager
import org.hackillinois.android.common.JWTUtilities
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.database.entity.Roles
import org.hackillinois.android.model.auth.JWT
import org.hackillinois.android.model.user.FavoritesResponse

class LoginActivity : AppCompatActivity() {
    private val authUriTemplate: String = "https://adonix.hackillinois.org/auth/login/%s/?device=android"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
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

        // request notification permission, if not already permitted
        if (Build.VERSION.SDK_INT > 32) {
            checkForNotificationPermission()
        }
    }

    private fun checkForNotificationPermission() {
        when {
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED -> {
                // nothing to be done
            }
            else -> {
                val requestNotificationPermission =
                    registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                        if (!isGranted) {
                            Log.e("NOTIFS PERMISSION", "denied")
                        }
                    }
                // You can directly ask for the permission.
                try {
                    requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                } catch (e: java.lang.Exception) {
                    Log.e("NOTIFS PERMISSION ERROR", "${e.message}")
                }
            }
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

                // verify user's roles are correct
                if (getOAuthProvider() == "google") {
                    verifyRole(api, jwt.token, "STAFF")
                } else {
                    verifyRole(api, jwt.token, "ATTENDEE")
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
                Log.d("ROLES", "${loginRoles.roles}")

                // Check if user's roles are correct. If not, display corresponding error message
                if (loginRoles.roles.contains(role)) {
                    getFavoritedEvents(api)
                    JWTUtilities.writeJWT(applicationContext, jwt) // save JWT to sharedPreferences for auto-login in the future
                    launchMainActivity()
                } else {
                    if (role == "STAFF") {
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

    private fun getFavoritedEvents(api: API) {
        // make api call to get list of event ids that user has favorited
        GlobalScope.launch {
            try {
                val response: FavoritesResponse = api.favoriteEvents()
                // loop through favorited event ids, wrap as Event object, and store in SharedPreferences
                for (eventId in response.following) {
                    val dummyEvent = Event(eventId, "", "", 0, 0, emptyList(), "", "", "0", false, false, false, "", false)
                    FavoritesManager.favoriteEvent(applicationContext, dummyEvent)
                }
                Log.d("Fetched favorites", response.toString())
            } catch (e: Exception) {
                Log.d("Fetched favorites ERROR", e.message.toString())
            }
        }
    }

    private fun showFailedToLogin(message: String?) {
        if (message != null) {
            Snackbar.make(findViewById(android.R.id.content), message.toString(), Snackbar.LENGTH_SHORT).show()
        } else {
            val failMessage = applicationContext.getString(R.string.login_fail_message)
            Snackbar.make(findViewById(android.R.id.content), failMessage, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun showFailedToLoginStaff() {
        val staffMessage = applicationContext.getString(R.string.login_fail_staff_message)
        Snackbar.make(findViewById(android.R.id.content), staffMessage, Snackbar.LENGTH_SHORT).show()
    }

    private fun showFailedToLoginAttendee() {
        val attendeeMessage = applicationContext.getString(R.string.login_fail_attendee_message)
        Snackbar.make(findViewById(android.R.id.content), attendeeMessage, Snackbar.LENGTH_SHORT).show()
    }

    private fun launchMainActivity() {
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    private fun setOAuthProvider(provider: String) {
        val prefString = applicationContext.getString(R.string.authorization_pref_file_key)
        val editor = applicationContext.getSharedPreferences(prefString, Context.MODE_PRIVATE).edit()
        editor.putString("provider", provider).apply()
    }

    private fun getOAuthProvider(): String {
        val prefString = applicationContext.getString(R.string.authorization_pref_file_key)
        return applicationContext.getSharedPreferences(prefString, Context.MODE_PRIVATE).getString("provider", "")
            ?: ""
    }
}
