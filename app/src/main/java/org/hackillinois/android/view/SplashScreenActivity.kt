package org.hackillinois.android.view

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import org.hackillinois.android.R

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
