package com.capstone.signora.ui.frontend.tutorial

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.capstone.signora.R

class TutorialViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val context: Context
) : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TutorialFragment.newInstance(R.drawable.slide1)
            1 -> TutorialFragment.newInstance(R.drawable.slide2)
            2 -> TutorialFragment.newInstance(R.drawable.slide3)
            3 -> TutorialFragment.newInstance(R.drawable.slide4)
            4 -> TutorialFragment.newInstance(R.drawable.slide5)
            else -> TutorialFragment.newInstance(R.drawable.slide6)
        }
    }

    override fun getItemCount(): Int {
        return 6
    }
}