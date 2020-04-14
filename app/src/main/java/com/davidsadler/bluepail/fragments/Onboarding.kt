package com.davidsadler.bluepail.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.davidsadler.bluepail.R
import com.davidsadler.bluepail.util.SHARED_PREF_ONBOARDING_BOOL
import kotlinx.android.synthetic.main.fragment_onboarding.*

class Onboarding: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        button_get_started.setOnClickListener {
            updateSharedOnboardingBool()
            val action = OnboardingDirections.actionOnboardingToPlantList()
            findNavController().navigate(action)
        }
    }

    private fun updateSharedOnboardingBool() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putBoolean(SHARED_PREF_ONBOARDING_BOOL, true)
            commit()
        }
    }
}