package com.noamrault.chatapp.ui.main

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.noamrault.chatapp.MainActivity
import com.noamrault.chatapp.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "theme") {
                activity?.recreate()
            }
        }

        PreferenceManager.getDefaultSharedPreferences(requireActivity())
            .registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onStart() {
        super.onStart()

        (activity as MainActivity).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    override fun onDestroy() {
        super.onDestroy()

        (activity as MainActivity).setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }
}