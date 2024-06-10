package com.capstone.signora.ui.frontend.pager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.capstone.signora.R
import com.capstone.signora.databinding.ActivityPagerBinding
import com.capstone.signora.ui.frontend.auth.RegisterActivity
import com.capstone.signora.ui.frontend.home.WelcomeActivity
import com.google.android.material.tabs.TabLayoutMediator

class PagerActivity : AppCompatActivity() {

    private lateinit var mViewPager: ViewPager2
    private lateinit var btnCreateAccount: Button
    private lateinit var binding: ActivityPagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPagerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        btnCreateAccount = binding.btnCreateAccount
        btnCreateAccount.setOnClickListener {
            val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putBoolean("isFirstRun", false)
                apply()
            }
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        mViewPager = findViewById(R.id.viewPager)
        mViewPager.adapter = ViewPagerAdapter(this, this)
        TabLayoutMediator(binding.pageIndicator, mViewPager) { _, _ -> }.attach()
        mViewPager.offscreenPageLimit = 1
    }
}