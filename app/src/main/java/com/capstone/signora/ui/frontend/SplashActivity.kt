package com.capstone.signora.ui.frontend

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.capstone.signora.R
import com.capstone.signora.ui.frontend.pager.PagerActivity

class SplashActivity : AppCompatActivity() {
    private val splash_delay: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        Handler().postDelayed({
            startActivity(Intent(this, PagerActivity::class.java))
            finish()
        }, splash_delay)
    }
}