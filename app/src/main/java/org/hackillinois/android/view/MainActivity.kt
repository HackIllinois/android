package org.hackillinois.android.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.layout_qr_sheet.*
import kotlinx.android.synthetic.main.layout_qr_sheet.view.*
import org.hackillinois.android.App
import org.hackillinois.android.R
import org.hackillinois.android.common.FavoritesManager
import org.hackillinois.android.common.JWTUtilities
import org.hackillinois.android.common.QRUtilities
import org.hackillinois.android.database.entity.Attendee
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.database.entity.QR
import org.hackillinois.android.database.entity.User
import org.hackillinois.android.notifications.FirebaseTokenManager
import org.hackillinois.android.view.groupmatching.GroupmatchingFragment
import org.hackillinois.android.view.home.HomeFragment
import org.hackillinois.android.view.maps.MapsFragment
import org.hackillinois.android.view.profile.ProfileFragment
import org.hackillinois.android.view.project.ProjectFragment
import org.hackillinois.android.view.schedule.ScheduleFragment
import org.hackillinois.android.viewmodel.MainViewModel
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    private var currentSelection = 0

    var groupMatchingSelectedProfile : Profile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupBottomAppBar()
        setupBottomQRSheet()

        val startFragment = HomeFragment()
        supportFragmentManager.beginTransaction().replace(R.id.contentFrame, startFragment).commit()

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java).apply {
            init()
            val owner = this@MainActivity
            qr.observe(owner, Observer { updateQrView(it) })
            user.observe(owner, Observer { user -> updateUserInformation(user) })
            attendee.observe(owner, Observer { attendee -> updateAttendeeInformation(attendee) })
        }

        updateFirebaseToken()
    }

    private fun setupBottomAppBar() {
        val selectedIconColor = ContextCompat.getColor(this, R.color.selectedAppBarIcon)
        val unselectedIconColor = ContextCompat.getColor(this, R.color.unselectedAppBarIcon)

        val bottomBarButtons = listOf(
                bottomAppBar.homeButton,
                bottomAppBar.scheduleButton,
                bottomAppBar.mapsButton,
                bottomAppBar.projectsButton
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
                        bottomAppBar.mapsButton -> switchFragment(ProfileFragment(), false)
                        bottomAppBar.projectsButton -> switchFragment(GroupmatchingFragment(), false)
                        else -> return@setOnClickListener
                    }
                }
            }
        }
    }

    private fun setupBottomQRSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(standardBottomSheet)
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        qr_fab.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            qr_fab.hide()
        }

        closeButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        logoutButton.setOnClickListener { logout() }

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(view: View, p1: Float) {}

            override fun onStateChanged(view: View, state: Int) {
                when (state) {
                    BottomSheetBehavior.STATE_HIDDEN -> qr_fab.show()
                    BottomSheetBehavior.STATE_EXPANDED -> qr_fab.hide()
                }
            }
        })
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

    private fun updateQrView(qr: QR?) = qr?.let {
        val text = qr.qrInfo
        val bitmap = generateQR(text)
        bitmap?.let {
            qrImageView?.setImageBitmap(bitmap)
        }
        qrImageView?.setBackgroundColor(Color.WHITE)
        loginNoticeTextView.visibility = View.GONE
    }

    private fun updateUserInformation(user: User?) = user?.let {
        standardBottomSheet.nameTextView?.let { view ->
            if (view.text.isNullOrBlank()) {
                view.text = user.fullName.toUpperCase()
            }
        }
    }

    private fun updateAttendeeInformation(attendee: Attendee?) = attendee?.let {
        standardBottomSheet.nameTextView?.text = attendee.fullName.toUpperCase()
    }

    private fun generateQR(text: String): Bitmap? {
        val width = qrImageView?.width ?: 0
        val height = qrImageView?.height ?: 0
        if (width == 0 || height == 0) { return null }
        val clearColor = Color.WHITE
        val solidColor = ContextCompat.getColor(this, R.color.colorPrimary)
        return QRUtilities.generateQRCode(text, width, height, clearColor, solidColor)
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
}
