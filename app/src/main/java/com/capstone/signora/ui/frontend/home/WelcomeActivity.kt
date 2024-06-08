package com.capstone.signora.ui.frontend.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.capstone.signora.R
import com.capstone.signora.ui.frontend.auth.LoginActivity
import com.capstone.signora.ui.frontend.auth.RegisterActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val masukButton: AppCompatButton = findViewById(R.id.button_masuk)
        val daftarButton: AppCompatButton = findViewById(R.id.button_daftar)

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