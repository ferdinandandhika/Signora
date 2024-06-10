package com.capstone.signora.ui.frontend.latihan

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.signora.R

class QuizActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quiz)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        val cardMudah = findViewById<CardView>(R.id.cardMudah)
        cardMudah.setOnClickListener {
            val intent = Intent(this, LatihanActivity::class.java)
            intent.putExtra(Constants.QUESTION_TYPE, Constants.TYPE_MUDAH)
            startActivity(intent)
        }

        val cardSedang = findViewById<CardView>(R.id.cardSedang)
        cardSedang.setOnClickListener {
            val intent = Intent(this, LatihanActivity::class.java)
            intent.putExtra(Constants.QUESTION_TYPE, Constants.TYPE_SEDANG)
            startActivity(intent)
        }

        val cardSulit = findViewById<CardView>(R.id.cardSulit)
        cardSulit.setOnClickListener {
            val intent = Intent(this, LatihanActivity::class.java)
            intent.putExtra(Constants.QUESTION_TYPE, Constants.TYPE_SULIT)
            startActivity(intent)
        }
    }
}