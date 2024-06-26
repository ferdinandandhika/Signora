package com.capstone.signora.ui.frontend.pager

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.capstone.signora.R

class ViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val context: Context
) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PagerFragment.newInstance(
                context.resources.getString(R.string.title_onboarding_1),
                context.resources.getString(R.string.description_onboarding_1),
                R.raw.pager8
            )
            1 -> PagerFragment.newInstance(
                context.resources.getString(R.string.title_onboarding_5),
                context.resources.getString(R.string.description_onboarding_5),
                R.raw.lottie_messaging
            )
            2 -> PagerFragment.newInstance(
                context.resources.getString(R.string.title_onboarding_3),
                context.resources.getString(R.string.description_onboarding_3),
                R.raw.lottie_developer
            )
            3 -> PagerFragment.newInstance(
                context.resources.getString(R.string.title_onboarding_4),
                context.resources.getString(R.string.description_onboarding_4),
                R.raw.page4
            )
            else -> PagerFragment.newInstance(
                context.resources.getString(R.string.title_onboarding_2),
                context.resources.getString(R.string.description_onboarding_2),
                R.raw.pager2
            )
        }
    }

    override fun getItemCount(): Int {
        return 5
    }
}