package org.hackillinois.android.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
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
import org.hackillinois.android.view.profile.ProfileFragment
import org.hackillinois.android.view.scanner.AttendeeScannerFragment
import org.hackillinois.android.view.scanner.StaffScannerFragment
import org.hackillinois.android.view.schedule.ScheduleFragment
import org.hackillinois.android.viewmodel.MainViewModel
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    private var currentSelection = 0
    private var onScanner = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupBottomAppBar()
        setupScannerButton()

        val startFragment = HomeFragment()
        supportFragmentManager.beginTransaction().replace(R.id.contentFrame, startFragment).commit()

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java).apply {
            init()
            val owner = this@MainActivity
        }
        updateFirebaseToken()
    }

    private fun setupBottomAppBar() {
        // by default, home button is selected
        val selectedIconColor = ContextCompat.getColor(this, R.color.selectedAppBarIcon)
        val unselectedIconColor = ContextCompat.getColor(this, R.color.unselectedAppBarIcon)

        bottomAppBar.homeButton.setColorFilter(selectedIconColor)

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
                onScanner = false

                if (newSelection != currentSelection) {
                    currentSelection = newSelection

                    // change nav bar color (will remove once navbar color finalized)
                    val nightBlue = ContextCompat.getColor(this, R.color.nightBlue)
                    val window = window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.statusBarColor = nightBlue
                    bottomAppBar.setBackgroundColor(nightBlue)
                    bottomBarButtons.forEach { (it as ImageButton).setBackgroundColor(nightBlue) }
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

    private fun setupScannerButton() {
        code_entry_fab.setOnClickListener {
            // set currentSelection to invalid index since scanner was selected
            currentSelection = -1

            // ensure that user is staff or attendee
            if (!hasLoggedIn()) {
                val toast = Toast.makeText(applicationContext, getString(R.string.scanner_not_logged_in_message), Toast.LENGTH_LONG)
                toast.show()
            } else {
                val staffScannerFragment = StaffScannerFragment()
                val attendeeScannerFragment = AttendeeScannerFragment()

                // set all bottom bar buttons to be the unselected color
                val bottomBarButtons = listOf(
                    bottomAppBar.homeButton,
                    bottomAppBar.scheduleButton,
                    bottomAppBar.leaderboard,
                    bottomAppBar.profile,
                )
                val unselectedIconColor = ContextCompat.getColor(this, R.color.unselectedAppBarIcon)
                bottomBarButtons.forEach { (it as ImageButton).setColorFilter(unselectedIconColor) }

                // if not already on scanner selection page, switch fragment
                if (!onScanner) {
                    // change navbar color (will remove once navbar color finalized)
                    val darkForest = ContextCompat.getColor(this, R.color.darkForest)
                    bottomAppBar.setBackgroundColor(darkForest)
                    bottomBarButtons.forEach { (it as ImageButton).setBackgroundColor(darkForest) }
                    val window = window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.statusBarColor = darkForest

                    // if staff, send them to staff scanner selection fragment
                    if (isStaff()) {
                        switchFragment(staffScannerFragment, false)
                    }
                    // if attendee, send them to attendee scanner selection fragment
                    else {
                        switchFragment(attendeeScannerFragment, true)
                    }
                }
            }
            onScanner = true
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
            val prefString = applicationContext.getString(R.string.authorization_pref_file_key)
            applicationContext.getSharedPreferences(prefString, Context.MODE_PRIVATE).edit().remove("provider").apply()
            App.database.clearAllTables()
            App.getAPI("")

            runOnUiThread {
                val loginIntent = Intent(this, LoginActivity::class.java)
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
        val prefString = applicationContext.getString(R.string.authorization_pref_file_key)
        return applicationContext.getSharedPreferences(prefString, Context.MODE_PRIVATE).getString("provider", "") ?: "" == "google"
    }
}
