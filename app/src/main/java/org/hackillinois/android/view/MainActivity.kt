package org.hackillinois.android.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
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
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Attendee
import org.hackillinois.android.database.entity.User
import org.hackillinois.android.viewmodel.MainViewModel
import org.hackillinois.android.view.home.HomeFragment
import org.hackillinois.android.view.schedule.ScheduleFragment

class MainActivity : AppCompatActivity(), View.OnClickListener {

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

        navViews = listOf(navHome, navSchedule, navOutdoorMaps, navIndoorMaps, navProfile)
        navViews.forEach { it.setOnClickListener(this) }

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.init()
        viewModel.user.observe(this, Observer { updateUserInfo(it) })
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

        val fragment = when (v) {
            navHome -> HomeFragment()
            navSchedule -> ScheduleFragment()
            navOutdoorMaps -> OutdoorMapsFragment()
            navIndoorMaps -> IndoorMapsFragment()
            navProfile -> ProfileFragment()
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
}
