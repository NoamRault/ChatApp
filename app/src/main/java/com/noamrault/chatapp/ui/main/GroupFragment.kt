package com.noamrault.chatapp.ui.main

import android.os.Bundle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.noamrault.chatapp.MainActivity

class GroupFragment : Fragment() {

    override fun onStart() {
        super.onStart()

        (activity as MainActivity).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    override fun onDestroy() {
        super.onDestroy()

        (activity as MainActivity).setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }
}