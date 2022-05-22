package com.noamrault.chatapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.noamrault.chatapp.data.LoginDataSource
import com.noamrault.chatapp.data.LoginRepository
import com.noamrault.chatapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val loginRepo: LoginRepository = LoginRepository(LoginDataSource())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!loginRepo.isLoggedIn) {
            authenticate()
        }

        setNavHeaderName()

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<NavigationView>(R.id.nav_view)
            .setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_register_fragment, R.id.nav_login_fragment, R.id.nav_main_fragment
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

    override fun onResume() {
        super.onResume()
        setNavHeaderName()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun authenticate() {
        val intent = Intent(this, AuthActivity::class.java).apply { }
        startActivity(intent)
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
}