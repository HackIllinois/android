package org.hackillinois.android.view

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import org.hackillinois.android.App
import org.hackillinois.android.R
import org.hackillinois.android.common.FavoritesManager
import org.hackillinois.android.common.JWTUtilities
import org.hackillinois.android.notifications.FirebaseTokenManager
import org.hackillinois.android.view.home.HomeFragment
import org.hackillinois.android.view.maps.MapsFragment
import org.hackillinois.android.view.project.ProjectFragment
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
                bottomAppBar.teamMatching
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
                        bottomAppBar.profile -> switchFragment(MapsFragment(), false)
                        bottomAppBar.teamMatching -> switchFragment(ProjectFragment(), false)
                        else -> return@setOnClickListener
                    }
                }
            }
        }
    }

    private fun setupCodeEntrySheet() {
        val inflater : LayoutInflater = layoutInflater
        val codeEnterView = inflater.inflate(R.layout.layout_event_code_dialog, null)
        val alertDialogBuilder = AlertDialog.Builder(this, R.style.WrapContentDialog)
        alertDialogBuilder.setView(codeEnterView)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val closeButton = codeEnterView.findViewById<ImageButton>(R.id.codeEntryClose)

        code_entry_fab.setOnClickListener {
            alertDialog.show()
        }

        closeButton.setOnClickListener {
            alertDialog.dismiss()
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
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            Log.d("MainActivity", instanceIdResult.token)
            FirebaseTokenManager.writeToken(applicationContext, instanceIdResult.token)
            FirebaseTokenManager.sendTokenToServerIfNew(applicationContext)
        }
    }

//    private fun logout() {
//        JWTUtilities.clearJWT(applicationContext)
//
//        thread {
//            FavoritesManager.clearFavorites(this)
//            App.database.clearAllTables()
//            App.getAPI("")
//
//            runOnUiThread {
//                val loginIntent = Intent(this, LoginActivity::class.java)
//                loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                startActivity(loginIntent)
//                finish()
//            }
//        }
//    }
}
