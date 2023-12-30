package org.hackillinois.android.view

import android.animation.Animator
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_splash_screen.splashAnimationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.hackillinois.android.App
import org.hackillinois.android.BuildConfig
import org.hackillinois.android.R
import org.hackillinois.android.common.JWTUtilities
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class SplashScreenActivity : AppCompatActivity() {

    private val countDownLatch = CountDownLatch(3)
    private var needsToLogin = true
    private var needsToUpdate = false
    private var hasInternet = true
    private var hasClickedOrAnimFinish = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // check if user has an internet connection
        hasInternet = isNetworkAvailable(this.applicationContext)
        if (!hasInternet) {
            showInternetPopup()
        }

        // if user taps animation, increment CountDown latch (to avoid waiting for anim to finish)
        splashAnimationView.setOnClickListener {
            countDownLatchIfTappedOrAnimationFinished()
        }

        // launch Coroutine to execute asynchronous calls
        makeAPICalls()

        thread {
            /* countDownLatch needs 3 things to finish:
               1. Version is >= API's stored version (GET /version/android/)
               2. Async api call (GET /user/) is completed or fails
               3. Loading animation finishes or user taps it
            */
            countDownLatch.await()
            // once countDownLatch is fulfilled, run logic for log in on the UI Thread
            runOnUiThread {
                splashAnimationView.pauseAnimation()
                if (needsToLogin) {
                    launchOnboardingActivity()
                } else {
                    launchMainActivity()
                }
            }
        }
    }

    private fun makeAPICalls() {
        val androidVersion = BuildConfig.VERSION_NAME
        playAnimation()
        lifecycleScope.launch {
            try {
                val api = App.getAPI()
                val apiResponse = withContext(Dispatchers.Default) { api.versionCode() }
                val apiVersion = apiResponse.version
                // check if user needs to update their app
                if (androidVersion < apiVersion) {
                    needsToUpdate = true
                    showUpdatePopup()
                } else {
                    // app is up-to-date, so start animation and check if they need to log in
                    countDownLatch.countDown()
                    val jwt = JWTUtilities.readJWT(applicationContext)
                    if (jwt != JWTUtilities.DEFAULT_JWT) {
                        Log.d("JWT SplashScreen", jwt)
                        val api = App.getAPI(jwt)
                        withContext(Dispatchers.Default) { api.user() }
                        needsToLogin = false
                        countDownLatch.countDown()
                    } else {
                        needsToLogin = true
                        countDownLatch.countDown()
                    }
                }
            } catch (e: java.net.UnknownHostException) {
                // make sure popup isn't already shown
                if (hasInternet) {
                    hasInternet = false
                    showInternetPopup()
                }
            } catch (e: Exception) {
                needsToLogin = true
                countDownLatch.countDown()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (needsToUpdate) {
            showUpdatePopup()
        } else if (!hasInternet) {
            showInternetPopup()
        }
    }

    private fun playAnimation() {
        // make animation view visible and start playing it
        splashAnimationView.visibility = View.VISIBLE
        splashAnimationView.playAnimation()
        splashAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator) {}
            override fun onAnimationStart(p0: Animator) {}

            override fun onAnimationEnd(p0: Animator) {
                countDownLatchIfTappedOrAnimationFinished()
            }

            override fun onAnimationCancel(p0: Animator) {
                countDownLatchIfTappedOrAnimationFinished()
            }
        })
    }

    private fun countDownLatchIfTappedOrAnimationFinished() {
        // increment countDownLatch ONCE if animation finishes or user taps it first
        synchronized(hasClickedOrAnimFinish) {
            if (!hasClickedOrAnimFinish) {
                hasClickedOrAnimFinish = true
                countDownLatch.countDown()
            }
        }
    }

    private fun launchMainActivity() {
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    private fun launchOnboardingActivity() {
        val mainIntent = Intent(this, OnboardingActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                // for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                // for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }

    private fun showInternetPopup() {
        splashAnimationView.pauseAnimation()
        splashAnimationView.visibility = View.INVISIBLE
        val builder = AlertDialog.Builder(this)
            .setTitle(R.string.need_internet_title)
            .setMessage(R.string.need_internet_message)
            .setCancelable(false)
            .setNegativeButton("Close app") { dialog, id ->
                exitProcess(0)
            }
        val alertDialog = builder.create()
        val buttonColor = ContextCompat.getColor(this, R.color.floralMauve)
        alertDialog.show()
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(buttonColor)
    }

    private fun showUpdatePopup() {
        splashAnimationView.pauseAnimation()
        splashAnimationView.visibility = View.INVISIBLE
        val builder = AlertDialog.Builder(this)
            .setTitle(R.string.update_app_title)
            .setMessage(R.string.update_app_message)
            .setCancelable(false)
            .setNegativeButton("Go to Play Store") { dialog, id ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=org.hackillinois.android.release&pcampaignid=web_share"))
                startActivity(intent)
            }
        val alertDialog = builder.create()
        val buttonColor = ContextCompat.getColor(this, R.color.floralMauve)
        alertDialog.show()
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(buttonColor)
    }
}
