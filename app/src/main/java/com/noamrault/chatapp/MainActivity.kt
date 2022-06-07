package com.noamrault.chatapp

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.multidex.BuildConfig
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.android.material.navigation.NavigationView
import com.noamrault.chatapp.data.AppDatabase
import com.noamrault.chatapp.data.Converters
import com.noamrault.chatapp.data.auth.LoginDataSource
import com.noamrault.chatapp.data.auth.LoginRepository
import com.noamrault.chatapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val requiredPermissions: Array<String> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        }
    private val requestCodeRequiredPermissions = 1

    private val loginRepo: LoginRepository = LoginRepository(LoginDataSource())
    private lateinit var userId: String

    private val serviceId = BuildConfig.APPLICATION_ID
    private val strategy = Strategy.P2P_STAR

    // Callbacks for receiving payloads
    private val payloadCallback: PayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {

        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {

        }
    }

    internal class ReceiveBytesPayloadListener : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            // This always gets the full data of the payload. Is null if it's not a BYTES payload.
            if (payload.type == Payload.Type.BYTES) {
                val message = String(payload.asBytes()!!)
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, p1: PayloadTransferUpdate) {
            // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
            // after the call to onPayloadReceived().
        }
    }

    // Callbacks for connections to other devices
    private val connectionLifecycleCallback: ConnectionLifecycleCallback =
        object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
                Log.i(ContentValues.TAG, "onConnectionInitiated: accepting connection")

                // Automatically accept the connection on both sides.
                // Nearby.getConnectionsClient(this@MainActivity).acceptConnection(endpointId, payloadCallback)

                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Accept connection to " + info.endpointName)
                    .setMessage("Confirm the code matches on both devices: " + info.authenticationDigits)
                    .setPositiveButton(
                        R.string.dialog_accept
                    ) { _: DialogInterface?, _: Int ->  // The user confirmed, so we can accept the connection.
                        Nearby.getConnectionsClient(this@MainActivity)
                            .acceptConnection(endpointId, payloadCallback)
                    }
                    .setNegativeButton(
                        R.string.dialog_cancel
                    ) { _: DialogInterface?, _: Int ->  // The user canceled, so we should reject the connection.
                        Nearby.getConnectionsClient(this@MainActivity)
                            .rejectConnection(endpointId)
                    }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                when (result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {}
                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {}
                    ConnectionsStatusCodes.STATUS_ERROR -> {}
                    else -> {}
                }
            }

            override fun onDisconnected(endpointId: String) {
                Log.i(ContentValues.TAG, "onDisconnected: disconnected from the opponent")
                // We've been disconnected from this endpoint. No more data can be sent or received.
            }
        }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        // Change the theme according to the user's settings
        setTheme()

        super.onCreate(savedInstanceState)

        // Launch the Authentication Activity if not logged in
        if (!loginRepo.isLoggedIn) {
            authenticate()
        } else {
            userId = loginRepo.user!!.uid

            // Start Advertising to receive messages
            startAdvertising()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        drawerLayout = binding.drawerLayout
        setContentView(binding.root)

        // Get the local database
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "chatapp-database"
        ).build()

        // Set the username and email in the navigation drawer's header
        setNavHeaderName()

        setSupportActionBar(binding.appBarMain.toolbarMain)

        val navView: NavigationView = binding.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<NavigationView>(R.id.nav_view).setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home
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

    override fun onStart() {
        super.onStart()

        if (!hasPermissions(this, requiredPermissions)) {
            requestPermissions(requiredPermissions, requestCodeRequiredPermissions)
        }
    }

    /** Returns true if the app was granted all the permissions. Otherwise, returns false.  */
    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    /** Handles user acceptance (or denial) of our permission request.  */
    @CallSuper
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != requestCodeRequiredPermissions) {
            return
        }
        for ((i, grantResult) in grantResults.withIndex()) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                Log.i(ContentValues.TAG, "Failed to request the permission " + permissions[i])
                Toast.makeText(this, "Missing Permissions", Toast.LENGTH_LONG).show()
                finish()
                return
            }
        }
        this.recreate()
    }

    /** Start Advertising to receive messages */
    private fun startAdvertising() {
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(strategy).build()
        Nearby.getConnectionsClient(this)
            .startAdvertising(
                userId, serviceId, connectionLifecycleCallback, advertisingOptions
            )
            .addOnSuccessListener { unused: Void? -> }
            .addOnFailureListener { e: Exception? -> }
    }

    override fun onBackPressed() {
        // Close the drawer when back is pressed and the drawer is open
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

    /** Launch the Authentication Activity */
    private fun authenticate() {
        val intent = Intent(this, AuthActivity::class.java).apply { }
        startActivity(intent)
        finish()
    }

    /** Set the username and email in the navigation drawer's header */
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

    /** Allows the user to open the drawer or prevents the user from opening the drawer */
    fun setDrawerLockMode(lockMode: Int) {
        drawerLayout.setDrawerLockMode(lockMode)
    }

    /** Allows fragments to change ActionBar's title */
    fun setActionBarTitle(title: String?) {
        binding.appBarMain.toolbarMain.title = title
    }
}