package org.hackillinois.android.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
// import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.FirebaseApp
// import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import org.hackillinois.android.App
import org.hackillinois.android.R
import org.hackillinois.android.common.FavoritesManager
import org.hackillinois.android.common.JWTUtilities
import org.hackillinois.android.notifications.FirebaseTokenManager
import org.hackillinois.android.view.home.HomeFragment
import org.hackillinois.android.view.leaderboard.LeaderboardFragment
import org.hackillinois.android.view.onboarding.OnboardingActivity
import org.hackillinois.android.view.profile.ProfileFragment
import org.hackillinois.android.view.scanner.ScannerFragment
import org.hackillinois.android.view.schedule.ScheduleFragment
import org.hackillinois.android.viewmodel.MainViewModel
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    private var currentSelection = 0

//    var groupMatchingSelectedProfile: Profile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupBottomAppBar()
        setupCodeEntrySheet()

        val startFragment = HomeFragment()
        supportFragmentManager.beginTransaction().replace(R.id.contentFrame, startFragment).commit()

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java).apply {
            init()
            val owner = this@MainActivity
        }
        updateFirebaseToken()
    }

    private fun setupBottomAppBar() {
        val selectedIconColor = ContextCompat.getColor(this, R.color.selectedAppBarIcon)
        val unselectedIconColor = ContextCompat.getColor(this, R.color.unselectedAppBarIcon)

        val bottomBarButtons = listOf(
            bottomAppBar.homeButton,
            bottomAppBar.scheduleButton,
            bottomAppBar.leaderboard,
            bottomAppBar.profile
        )

        // by default, home button is selected
        bottomAppBar.homeButton.setColorFilter(selectedIconColor)

        // make all buttons unselectedColor and then set selected button to selectedColor
        bottomBarButtons.forEach { button ->
            button.setOnClickListener { view ->
                val newSelection = bottomBarButtons.indexOf(button)
                if (newSelection != currentSelection) {
                    currentSelection = newSelection

                    bottomBarButtons.forEach { (it as ImageButton).setColorFilter(unselectedIconColor) }
                    (view as ImageButton).setColorFilter(selectedIconColor)

                    when (view) {
                        bottomAppBar.homeButton -> switchFragment(HomeFragment(), false)
                        bottomAppBar.scheduleButton -> switchFragment(ScheduleFragment(), false)
                        bottomAppBar.leaderboard -> switchFragment(LeaderboardFragment(), false)
                        bottomAppBar.profile -> switchFragment(ProfileFragment(), false)
                        else -> return@setOnClickListener
                    }
                }
            }
        }
    }

    private fun setupCodeEntrySheet() {
        val inflater: LayoutInflater = layoutInflater

        val scannerFragment = ScannerFragment()

        code_entry_fab.setOnClickListener {
            if (!hasLoggedIn()) {
                val toast = Toast.makeText(applicationContext, getString(R.string.login_error_msg), Toast.LENGTH_LONG)
//                ((toast.view as LinearLayout).getChildAt(0) as TextView).gravity = Gravity.CENTER_HORIZONTAL
                toast.show()
//                Snackbar.make(findViewById(android.R.id.content), getString(R.string.login_error_msg), Snackbar.LENGTH_SHORT).show()
            } else {
                switchFragment(scannerFragment, true)
            }
        }
    }

    fun switchFragment(fragment: Fragment, addToBackStack: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contentFrame, fragment)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    fun switchFragmentWithAnimation(fragment: Fragment, addToBackStack: Boolean, anim: R.anim) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contentFrame, fragment)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    private fun updateFirebaseToken() {
        FirebaseApp.initializeApp(applicationContext)
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener { firebaseToken ->
            Log.d("MainActivity", firebaseToken)
            FirebaseTokenManager.writeToken(applicationContext, firebaseToken)
            FirebaseTokenManager.sendTokenToServerIfNew(applicationContext)
        }
    }

    fun logout() {
        JWTUtilities.clearJWT(applicationContext)

        thread {
            FavoritesManager.clearFavorites(this)
            App.database.clearAllTables()
            App.getAPI("")

            runOnUiThread {
//                val loginIntent = Intent(this, LoginActivity::class.java)
                val loginIntent = Intent(this, OnboardingActivity::class.java)
                loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(loginIntent)
                finish()
            }
        }
    }

    private fun hasLoggedIn(): Boolean {
        return JWTUtilities.readJWT(applicationContext) != JWTUtilities.DEFAULT_JWT
    }

    private fun isStaff(): Boolean {
        return applicationContext.getSharedPreferences(applicationContext.getString(R.string.authorization_pref_file_key), Context.MODE_PRIVATE).getString("provider", "")
            ?: "" == "google"
    }
}
