package com.example.foodtrack

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.foodtrack.R

class OnBoardingFragment : Fragment() {

    companion object {
        private const val ARG_POSITION = "position"

        fun newInstance(position: Int): OnBoardingFragment {
            return OnBoardingFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_POSITION, position)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val position = requireArguments().getInt(ARG_POSITION)
        val layoutId = when (position) {
            0 -> R.layout.fragment_onboarding_one
            1 -> R.layout.fragment_onboarding_two
            2 -> R.layout.fragment_onboarding_three
            else -> R.layout.fragment_onboarding_four
        }
        val rootView = inflater.inflate(layoutId, container, false)

        if (position == 3) {
            val fourthButton = rootView.findViewById<View>(R.id.buttonNext)
            fourthButton?.setOnClickListener {
                navigateToLogin()
            }
        }

        return rootView
    }


    private fun navigateToLogin() {
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
    }
}
