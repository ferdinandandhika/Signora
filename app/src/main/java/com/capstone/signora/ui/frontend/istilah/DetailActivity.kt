package com.capstone.signora.ui.frontend.istilah

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.signora.R

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val position = intent.getIntExtra("POSITION", -1)

        val alphabetDetailImageView: ImageView = findViewById(R.id.alphabet_detail_image)
        val extraImageView: ImageView = findViewById(R.id.extra_image)

        if (position != -1) {
            val alphabetTypedArray = resources.obtainTypedArray(R.array.gambarAlphabet)
            val isyaratTypedArray = resources.obtainTypedArray(R.array.gambarIsyarat)

            val alphabetImageResId = alphabetTypedArray.getResourceId(position, -1)
            val isyaratImageResId = isyaratTypedArray.getResourceId(position, -1)

            alphabetTypedArray.recycle()
            isyaratTypedArray.recycle()

            alphabetDetailImageView.setImageResource(alphabetImageResId)
            extraImageView.setImageResource(isyaratImageResId)
        }

        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
    }
}