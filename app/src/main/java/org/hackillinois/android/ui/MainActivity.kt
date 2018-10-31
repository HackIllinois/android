package org.hackillinois.android

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

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var navViews: List<View>

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

        v.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.selectedMenuItemColor))
        supportActionBar?.title = (v as TextView).text

        supportFragmentManager.beginTransaction().replace(R.id.contentFrame, fragment).commit()
        drawerLayout.closeDrawer(GravityCompat.START)
    }
}
