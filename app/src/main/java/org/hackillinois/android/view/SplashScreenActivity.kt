package org.hackillinois.android.view

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_splash_screen.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.hackillinois.android.App
import org.hackillinois.android.R
import org.hackillinois.android.common.JWTUtilities
import org.hackillinois.android.database.entity.User
import org.hackillinois.android.view.onboarding.OnboardingActivity
import java.lang.Exception
import java.net.SocketTimeoutException
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

class SplashScreenActivity : AppCompatActivity() {

    private val countDownLatch = CountDownLatch(2)
    private var needsToLogin = true
    @Volatile private var hasClickedOrAnimFinish = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

//        var anim = bodymovin.loadAnimation(params);
//        anim.frameRate = 25;

        Log.d("FRAME RATE", splashAnimationView.duration.toString())

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
        splashAnimationView.setOnClickListener {
            countDownLatchIfTappedOrAnimationFinished()
        }

        val jwt = JWTUtilities.readJWT(applicationContext)

        if (jwt != JWTUtilities.DEFAULT_JWT) {
            GlobalScope.launch {
                val api = App.getAPI(jwt)
                try {
                    val user: User = api.user()
                    // needsToLogin = response.code() != 200 (original code)
                    // if code is not 2--, a HttpException will be thrown
                    needsToLogin = false
                    withContext(Dispatchers.IO) {
                        try {
                            // TODO httpException 405
                            // api.updateNotificationTopics()
                        } catch (e: SocketTimeoutException) {
                            Log.e("LoginActivity", "Notifications update timed out!")
                        }
                    }
                    countDownLatch.countDown()
                } catch (e: Exception) {
                    Log.e("LoginActivity", "Failed to check is jwt is valid")
                    needsToLogin = true
                    countDownLatch.countDown()
                }
            }
        } else {
            needsToLogin = true
            countDownLatch.countDown()
        }

        thread {
            countDownLatch.await()

            runOnUiThread {
                if (needsToLogin) {
                    launchOnboardingActivity()
//                    launchLoginActivity()
                } else {
                    launchMainActivity()
                }
            }
        }
    }

    private fun countDownLatchIfTappedOrAnimationFinished() {
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

    private fun launchLoginActivity() {
        val mainIntent = Intent(this, LoginActivity::class.java)
        startActivity(mainIntent)
        finish()
    }
}
