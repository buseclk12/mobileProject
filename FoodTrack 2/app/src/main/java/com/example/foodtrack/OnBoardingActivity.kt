package com.example.foodtrack
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var buttonNext: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.viewPager)
        buttonNext = findViewById(R.id.buttonNext)

        val adapter = OnboardingAdapter(this)
        viewPager.adapter = adapter

        buttonNext.setOnClickListener {
            if (viewPager.currentItem < adapter.itemCount - 1) {
                viewPager.currentItem = viewPager.currentItem + 1
            } else {
                endOnboarding()
            }
        }
    }

    private fun endOnboarding() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private inner class OnboardingAdapter(fa: AppCompatActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 4

        override fun createFragment(position: Int): Fragment {
            return OnBoardingFragment.newInstance(position)
        }
    }
}