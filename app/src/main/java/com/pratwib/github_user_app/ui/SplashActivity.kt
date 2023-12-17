package com.pratwib.github_user_app.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.pratwib.github_user_app.ui.main.MainActivity

@Suppress("DEPRECATION")
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashTime: Long = 3000

        Handler().postDelayed({
            val intentSplashScreen = Intent(this, MainActivity::class.java)
            startActivity(intentSplashScreen)
            finish()
        }, splashTime)
    }
}