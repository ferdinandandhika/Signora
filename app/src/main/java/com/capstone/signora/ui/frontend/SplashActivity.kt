package com.capstone.signora.ui.frontend

import android.content.Context
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

    private val splash_delay: Long = 2000 // 2 detik

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
            val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)

            if (isFirstRun) {
                startActivity(Intent(this, PagerActivity::class.java))
            } else {
                val auth = FirebaseAuth.getInstance()
                val user = auth.currentUser

                if (user != null) {
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    startActivity(Intent(this, WelcomeActivity::class.java))
                }
            }
            finish()
        }, splash_delay)
    }
}