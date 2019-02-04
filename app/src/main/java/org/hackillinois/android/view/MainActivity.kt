package org.hackillinois.android.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_nav_menu.*
import kotlinx.android.synthetic.main.layout_nav_menu.view.*
import org.hackillinois.android.App
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Roles
import org.hackillinois.android.database.entity.User
import org.hackillinois.android.firebase.DeviceToken
import org.hackillinois.android.view.home.HomeFragment
import org.hackillinois.android.view.navigationdrawer.IndoorMapsFragment
import org.hackillinois.android.view.navigationdrawer.OutdoorMapsFragment
import org.hackillinois.android.view.navigationdrawer.ProfileFragment
import org.hackillinois.android.view.navigationdrawer.ScannerFragment
import org.hackillinois.android.view.navigationdrawer.admin.AdminFragment
import org.hackillinois.android.view.schedule.ScheduleFragment
import org.hackillinois.android.viewmodel.MainViewModel
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val defaultToken: String = ""

    private lateinit var navViews: List<View>
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
            title = "HOME"
        }

        val startFragment = HomeFragment()
        supportFragmentManager.beginTransaction().replace(R.id.contentFrame, startFragment).commit()

        navViews = listOf(navHome, navSchedule, navOutdoorMaps, navIndoorMaps, navProfile, navLogout, navAdmin, navScanner)
        navViews.forEach { it.setOnClickListener(this) }

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.init()
        viewModel.user.observe(this, Observer { updateUserInfo(it) })
        viewModel.roles.observe(this, Observer { updateRoles(it) })

        updateDeviceToken()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(v: View) {
        navViews.forEach { it.setBackgroundColor(Color.WHITE) }

        if (v == navLogout) {
            logout()
            return
        }

        val fragment = when (v) {
            navHome -> HomeFragment()
            navSchedule -> ScheduleFragment()
            navOutdoorMaps -> OutdoorMapsFragment()
            navIndoorMaps -> IndoorMapsFragment()
            navProfile -> ProfileFragment()
            navAdmin -> AdminFragment()
            navScanner -> ScannerFragment()
            else -> return
        }

        v.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.selectedMenuItem))
        supportActionBar?.title = (v as TextView).text

        supportFragmentManager.beginTransaction().replace(R.id.contentFrame, fragment).commit()
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun updateUserInfo(user: User?) {
        user?.let {
            navMenu.nameTextView.text = it.getFullName()
            navMenu.emailTextView.text = it.email
        }
    }

    /**
     * Modify the available options in the nav menu
     * Staff: Can access the scanner
     * Admins: Can access scanner and admin tools
     */
    private fun updateRoles(roles: Roles?) {
        var listOfRoles: List<String>? = roles?.roles ?: return
        if (listOfRoles!!.contains("Admin")) {
            navAdmin.visibility = View.VISIBLE
            navScanner.visibility = View.VISIBLE
        }
        if (listOfRoles!!.contains("Staff")) {
            navScanner.visibility = View.VISIBLE
        }
    }

    private fun logout() {
        clearJWT()

        thread {
            App.getDatabase().clearAllTables()

            runOnUiThread {
                val loginIntent = Intent(this, LoginActivity::class.java)
                loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(loginIntent)
                finish()
            }
        }
    }

    private fun clearJWT() {
        val editor = applicationContext.getSharedPreferences(applicationContext.getString(R.string.authorization_pref_file_key), Context.MODE_PRIVATE).edit()
        editor.putString("jwt", defaultToken)
        editor.apply()
    }

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
