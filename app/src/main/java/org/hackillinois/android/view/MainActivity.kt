package org.hackillinois.android.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseApp
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
import org.hackillinois.android.view.scanner.StaffScannerFragment
import org.hackillinois.android.view.schedule.ScheduleFragment
import org.hackillinois.android.viewmodel.MainViewModel
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    private var currentSelection = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupBottomAppBar()
        setupCodeEntrySheet()

        val startFragment = HomeFragment()
        supportFragmentManager.beginTransaction().replace(R.id.contentFrame, startFragment).commit()

//        ViewModelProvider(this).get(ProfileViewModel::class.java)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java).apply {
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
            bottomAppBar.profile,
        )

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
        code_entry_fab.setOnClickListener {
            if (!hasLoggedIn()) {
                val toast = Toast.makeText(applicationContext, getString(R.string.login_error_msg), Toast.LENGTH_LONG)
                toast.show()
            } else {
                // if staff, send them to fragment to select meeting attendance or attendee check-in
                // if attendee, send them right to the scanner fragment
                if (isStaff()) {
                    switchFragment(staffScannerFragment, true)
                } else {
                    switchFragment(scannerFragment, true)
                }
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
