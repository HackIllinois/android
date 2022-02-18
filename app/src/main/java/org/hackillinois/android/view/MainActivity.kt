package org.hackillinois.android.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.layout_event_code_dialog.*
import org.hackillinois.android.App
import org.hackillinois.android.R
import org.hackillinois.android.common.FavoritesManager
import org.hackillinois.android.common.JWTUtilities
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.notifications.FirebaseTokenManager
import org.hackillinois.android.view.groupmatching.GroupmatchingFragment
import org.hackillinois.android.view.home.HomeFragment
import org.hackillinois.android.view.profile.ProfileFragment
import org.hackillinois.android.view.schedule.ScheduleFragment
import org.hackillinois.android.viewmodel.MainViewModel
import java.lang.Exception
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    private var currentSelection = 0

    var groupMatchingSelectedProfile: Profile? = null

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
                bottomAppBar.profile,
                bottomAppBar.leaderboard
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
                        bottomAppBar.profile -> switchFragment(ProfileFragment(), false)
                        bottomAppBar.leaderboard -> switchFragment(LeaderboardFragment(), false)
                        else -> return@setOnClickListener
                    }
                }
            }
        }
    }

    private fun setupCodeEntrySheet() {
        val inflater: LayoutInflater = layoutInflater

//        val scannerFragmentView = inflater.inflate(R.layout.fragment_scanner, null)
//        val alertDialogBuilder = AlertDialog.Builder(this, R.style.WrapContentDialog)
//        alertDialogBuilder.setView(scannerFragmentView)
//        val alertDialog = alertDialogBuilder.create()
        val scannerFragment = ScannerFragment()
//        val closeButton = findViewById<ImageButton>(R.id.qrScannerClose)

        code_entry_fab.setOnClickListener {
            if (!hasLoggedIn()) {
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.fab_error_msg), Snackbar.LENGTH_SHORT).show()
            } else {
//                alertDialog.show()
                switchFragment(scannerFragment, true)
            }
        }

//
//        submitCodeButton.setOnClickListener {
//            // If not logged in
//
//            // Find the button
//            val enterCodeFieldText = codeEnterView.findViewById<EditText>(R.id.enterCodeField).text
//            val code: String = enterCodeFieldText.toString()
//            if (code.isEmpty()) {
//                alertDialog.dismiss()
//                Snackbar.make(findViewById(android.R.id.content), R.string.invalid_code, Snackbar.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            // Launch thread to send api request
//            thread {
//                GlobalScope.launch(Dispatchers.IO) {
//                    try {
//                        val response = EventRepository.checkInEvent(code)
//                        Snackbar.make(findViewById(android.R.id.content), response, Snackbar.LENGTH_SHORT).show()
//                    } catch (e: Exception) {
//                        Log.e("Code submit", e.toString())
//                    }
//                }
//            }
//
//            alertDialog.dismiss()
//        }
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
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            Log.d("MainActivity", instanceIdResult.token)
            FirebaseTokenManager.writeToken(applicationContext, instanceIdResult.token)
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
}
