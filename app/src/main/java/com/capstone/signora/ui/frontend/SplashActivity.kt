package com.capstone.signora.ui.frontend

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.capstone.signora.R
import com.capstone.signora.ui.frontend.home.MainActivity
import com.capstone.signora.ui.frontend.home.WelcomeActivity
import com.capstone.signora.ui.frontend.pager.PagerActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    private val splash_delay: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        Handler().postDelayed({
            if (isFirstRun) {

                startActivity(Intent(this, PagerActivity::class.java))
                sharedPreferences.edit().putBoolean("isFirstRun", false).apply()
            } else {

                val user = FirebaseAuth.getInstance().currentUser
                if (user != null && isLoggedIn) {
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    startActivity(Intent(this, WelcomeActivity::class.java))
                }
            }
            finish()
        }, splash_delay)
    }
}