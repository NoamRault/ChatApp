package com.noamrault.chatapp

import android.content.Intent
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import com.noamrault.chatapp.data.LoginDataSource
import com.noamrault.chatapp.data.LoginRepository
import com.noamrault.chatapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout

    private val loginRepo: LoginRepository = LoginRepository(LoginDataSource())

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        drawerLayout = binding.drawerLayout
        setContentView(binding.root)

        if (!loginRepo.isLoggedIn) {
            authenticate()
        }

        setNavHeaderName()

        setSupportActionBar(binding.appBarMain.toolbarMain)

        val navView: NavigationView = binding.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<NavigationView>(R.id.nav_view).setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home_fragment
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.menu.findItem(R.id.nav_logout).setOnMenuItemClickListener {
            loginRepo.logout()
            authenticate()
            true
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun authenticate() {
        val intent = Intent(this, AuthActivity::class.java).apply { }
        startActivity(intent)
        finish()
    }

    private fun setNavHeaderName() {
        val headerView = findViewById<NavigationView>(R.id.nav_view).getHeaderView(0)
        val usernameTextView = headerView.findViewById<TextView>(R.id.usernameView)
        val emailTextView = headerView.findViewById<TextView>(R.id.emailAddressView)

        if (loginRepo.user?.displayName != null && loginRepo.user?.email != null) {
            usernameTextView.text = loginRepo.user?.displayName
            emailTextView.text = loginRepo.user?.email
        }
    }

    private fun setTheme() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        when (preferences.getString("theme", "")) {
            "auto" -> setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
            "light" -> setDefaultNightMode(MODE_NIGHT_NO)
            "dark" -> setDefaultNightMode(MODE_NIGHT_YES)
        }
    }

    fun setDrawerLockMode(lockMode: Int) {
        drawerLayout.setDrawerLockMode(lockMode)
    }
}