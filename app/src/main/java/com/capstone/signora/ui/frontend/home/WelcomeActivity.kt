package com.capstone.signora.ui.frontend.home

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.capstone.signora.R
import com.capstone.signora.ui.frontend.auth.LoginActivity
import com.capstone.signora.ui.frontend.auth.RegisterActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val masukButton: ImageButton = findViewById(R.id.button_masuk)
        val daftarButton: ImageButton = findViewById(R.id.button_daftar)

        masukButton.setOnClickListener {
            val intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        daftarButton.setOnClickListener {
            val intent = Intent(this@WelcomeActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}