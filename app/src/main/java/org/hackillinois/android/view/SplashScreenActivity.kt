package org.hackillinois.android.view

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_splash_screen.*
import org.hackillinois.android.App
import org.hackillinois.android.R
import org.hackillinois.android.common.JWTUtilities
import org.hackillinois.android.database.entity.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

class SplashScreenActivity : AppCompatActivity() {

    private val countDownLatch = CountDownLatch(2)
    private var needsToLogin = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        splashAnimationView.playAnimation()
        splashAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) { }
            override fun onAnimationStart(p0: Animator?) { }

            override fun onAnimationEnd(p0: Animator?) {
                countDownLatch.countDown()
            }
            override fun onAnimationCancel(p0: Animator?) {
                countDownLatch.countDown()
            }
        })

        val jwt = JWTUtilities.readJWT(applicationContext)

        if (jwt != JWTUtilities.DEFAULT_JWT) {
            val api = App.getAPI(jwt)
            api.user().enqueue(object : Callback<User> {
                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("LoginActivity", "Failed to check is jwt is valid")
                    needsToLogin = true
                    countDownLatch.countDown()
                }

                override fun onResponse(call: Call<User>, response: Response<User>) {
                    needsToLogin = response.code() != 200
                    if (!needsToLogin) {
                        thread {
                            try {
                                api.updateNotificationTopics().execute()
                            } catch (e: SocketTimeoutException) {
                                Log.e("LoginActivity", "Notifications update timed out!")
                            }
                        }
                    }
                    countDownLatch.countDown()
                }
            })
        } else {
            needsToLogin = true
            countDownLatch.countDown()
        }

        thread {
            countDownLatch.await()

            runOnUiThread {
                if (needsToLogin) {
                    launchLoginActivity()
                } else {
                    launchMainActivity()
                }
            }
        }
    }

    fun launchMainActivity() {
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    fun launchLoginActivity() {
        val mainIntent = Intent(this, LoginActivity::class.java)
        startActivity(mainIntent)
        finish()
    }
}
