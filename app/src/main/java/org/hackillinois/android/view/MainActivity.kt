package org.hackillinois.android.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import org.hackillinois.android.App
import org.hackillinois.android.R
import org.hackillinois.android.notifications.DeviceToken
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

        val bottomSheetBehavior = BottomSheetBehavior.from(standardBottomSheet)

        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

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
                    bottomAppBar.mapButton -> MapsFragment()
                    bottomAppBar.groupButton -> MentorFragment()
                    else -> return@setOnClickListener
                }

                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.contentFrame, newFragment)
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.commit()
            }
        }

        qr_fab.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            qr_fab.hide()
        }

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) { }

            override fun onStateChanged(view: View, state: Int) {
                when (state) {
                    BottomSheetBehavior.STATE_HIDDEN -> qr_fab.show()
                    BottomSheetBehavior.STATE_COLLAPSED -> qr_fab.show()
                    BottomSheetBehavior.STATE_EXPANDED -> qr_fab.hide()
                }
            }
        })

        val startFragment = HomeFragment()
        supportFragmentManager.beginTransaction().replace(R.id.contentFrame, startFragment).commit()

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.init()

        updateDeviceToken()
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
