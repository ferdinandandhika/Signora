package com.capstone.signora.ui.frontend.tutorial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.capstone.signora.R
import com.capstone.signora.databinding.ActivityTutorialBinding
import com.capstone.signora.ui.frontend.auth.RegisterActivity
import com.capstone.signora.ui.frontend.home.MainActivity
import com.capstone.signora.ui.frontend.tutorial.TutorialViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class TutorialActivity : AppCompatActivity() {
    private lateinit var mViewPager: ViewPager2
    private lateinit var textSkip: TextView
    private lateinit var textEnd: TextView
    private lateinit var btnNextStep: ImageButton

    private lateinit var binding: ActivityTutorialBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mViewPager = binding.viewPager
        textEnd = binding.textEnd
        btnNextStep = binding.btnNextStep
        mViewPager.adapter = TutorialViewPagerAdapter(this, this)
        mViewPager.offscreenPageLimit = 1
        TabLayoutMediator(binding.pageIndicator, mViewPager) { _, _ -> }.attach()
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 4) { // Assuming 4 is the last position
                    btnNextStep.visibility = View.GONE
                    textEnd.visibility = View.VISIBLE
                } else {
                    btnNextStep.visibility = View.VISIBLE
                    textEnd.visibility = View.GONE
                }
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
            override fun onPageScrollStateChanged(arg0: Int) {}
        })

        textEnd.setOnClickListener {
            val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putBoolean("isFirstRun", false)
                apply()
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnNextStep.setOnClickListener {
            if (mViewPager.currentItem == mViewPager.adapter?.itemCount?.minus(1)) {
                // If it's the last item, go to MainActivity
                val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                with(sharedPreferences.edit()) {
                    putBoolean("isFirstRun", false)
                    apply()
                }
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Otherwise, go to the next item
                mViewPager.setCurrentItem(mViewPager.currentItem + 1, true)
            }
        }
    }

    private fun getItem(): Int {
        return mViewPager.currentItem
    }
}
