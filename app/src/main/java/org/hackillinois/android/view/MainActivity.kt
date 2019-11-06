package org.hackillinois.android.view

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.layout_nav_menu.*
import kotlinx.android.synthetic.main.layout_nav_menu.view.*
import org.hackillinois.android.App
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Roles
import org.hackillinois.android.database.entity.User
import org.hackillinois.android.notifications.DeviceToken
import org.hackillinois.android.view.admin.AdminFragment
import org.hackillinois.android.view.home.HomeFragment
import org.hackillinois.android.view.schedule.ScheduleFragment
import org.hackillinois.android.viewmodel.MainViewModel
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private val defaultToken: String = ""

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val selectedIconColor = ContextCompat.getColor(this, R.color.selectedAppBarIcon)
        val unselectedIconColor = ContextCompat.getColor(this, R.color.unselectedAppBarIcon)

        val bottomBarButtons = listOf(
            bottomAppBar.homeButton,
            bottomAppBar.todayButton,
            bottomAppBar.mapButton,
            bottomAppBar.groupButton
        )

        // by default, home button is selected
        bottomAppBar.homeButton.setColorFilter(selectedIconColor)
        
        // make all buttons unselectedColor and then set selected button to selectedColor
        bottomBarButtons.forEach { button ->
            button.setOnClickListener { view ->
                bottomBarButtons.forEach { (it as ImageButton).setColorFilter(unselectedIconColor) }
                (view as ImageButton).setColorFilter(selectedIconColor)

                val newFragment = when (view) {
                    bottomAppBar.homeButton -> HomeFragment()
                    bottomAppBar.todayButton -> ScheduleFragment()
                    bottomAppBar.mapButton -> OutdoorMapsFragment()
                    bottomAppBar.groupButton -> ProfileFragment()
                    else -> return@setOnClickListener
                }

                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.contentFrame, newFragment)
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.commit()
            }
        }

        val startFragment = HomeFragment()
        supportFragmentManager.beginTransaction().replace(R.id.contentFrame, startFragment).commit()

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.init()
        //viewModel.user.observe(this, Observer { updateUserInfo(it) })
        //viewModel.roles.observe(this, Observer { updateRoles(it) })

        updateDeviceToken()
    }

//    private fun updateUserInfo(user: User?) {
//        user?.let {
//            navMenu.nameTextView.text = it.fullName
//            navMenu.emailTextView.text = it.email
//        }
//    }
//
//    /**
//     * Modify the available options in the nav menu
//     * Staff: Can access the scanner
//     * Admins: Can access scanner and admin tools
//     */
//    private fun updateRoles(roles: Roles?) = roles?.let { roles ->
//        var listOfRoles: List<String> = roles.roles
//        if (listOfRoles.contains("Admin")) {
//            navAdmin.visibility = View.VISIBLE
//            navScanner.visibility = View.VISIBLE
//        }
//        if (listOfRoles.contains("Staff")) {
//            navScanner.visibility = View.VISIBLE
//        }
//    }
//
//    private fun logout() {
//        clearJWT()
//
//        thread {
//            App.database.clearAllTables()
//
//            runOnUiThread {
//                val loginIntent = Intent(this, LoginActivity::class.java)
//                loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                startActivity(loginIntent)
//                finish()
//            }
//        }
//    }
//
//    private fun clearJWT() {
//        val editor = applicationContext.getSharedPreferences(applicationContext.getString(R.string.authorization_pref_file_key), Context.MODE_PRIVATE).edit()
//        editor.putString("jwt", defaultToken)
//        editor.apply()
//    }
//
    private fun updateDeviceToken() {
        val token = applicationContext.getSharedPreferences(applicationContext.getString(R.string.authorization_pref_file_key), Context.MODE_PRIVATE).getString("firebaseToken", defaultToken)
                ?: defaultToken
        if (token != defaultToken) {
            thread {
                App.getAPI().sendUserToken(DeviceToken(token)).execute()

                val editor = applicationContext.getSharedPreferences(applicationContext.getString(R.string.authorization_pref_file_key), Context.MODE_PRIVATE).edit()
                editor.putString("firebaseToken", defaultToken)
                editor.apply()
            }
        }
    }
}
