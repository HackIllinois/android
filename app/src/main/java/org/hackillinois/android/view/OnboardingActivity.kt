package org.hackillinois.android.view

import OnboardingAdapter
import android.content.Intent
import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_onboarding.*
import org.hackillinois.android.R
import org.hackillinois.android.view.onboarding.OnboardingPageFragment

class OnboardingActivity : FragmentActivity() {

    private val images = listOf(
        R.drawable.transparent_image,
        R.drawable.onboarding_1,
        R.drawable.onboarding_2,
        R.drawable.onboarding_3,
        R.drawable.onboarding_4,
        R.drawable.onboarding_5,
        R.drawable.onboarding_6,
        R.drawable.onboarding_7
    )

    private val titles = listOf(
        R.string.onboarding_welcome_title,
        R.string.onboarding_countdown_title,
        R.string.onboarding_schedule_title,
        R.string.onboarding_check_in_title,
        R.string.onboarding_shop_title,
        R.string.onboarding_cart_title,
        R.string.onboarding_scan_title,
        R.string.onboarding_profile_title

    )

    private val descriptions = listOf(
        R.string.onboarding_welcome_description,
        R.string.onboarding_countdown_description,
        R.string.onboarding_schedule_description,
        R.string.onboarding_check_in_description,
        R.string.onboarding_shop_description,
        R.string.onboarding_cart_description,
        R.string.onboarding_scan_description,
        R.string.onboarding_profile_description
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_onboarding)

        view_pager.adapter = ScreenSlidePagerAdapter(this)
        view_pager.offscreenPageLimit = 1
//        view_pager.adapter = OnboardingAdapter(images)

        // connect toggled circle buttons to the view pager
        TabLayoutMediator(tab_layout, view_pager) { tab, position ->
            tab.select()
        }.attach()

        attendeeLoginBtn.setOnClickListener { launchLoginActivity() }
    }

    private fun launchLoginActivity() {
        val mainIntent = Intent(this, LoginActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = images.size

        // create OnboardingPageFragment that shows each fragment in the carousel
        override fun createFragment(position: Int): Fragment = OnboardingPageFragment.newInstance(
            imageRes = images[position],
            titleRes = titles[position],
            descriptionRes = descriptions[position],
        )
    }
}
