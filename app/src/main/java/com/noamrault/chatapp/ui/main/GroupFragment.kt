package com.noamrault.chatapp.ui.main

import android.app.AlertDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.multidex.BuildConfig
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.noamrault.chatapp.MainActivity
import com.noamrault.chatapp.R
import com.noamrault.chatapp.data.auth.LoginDataSource
import com.noamrault.chatapp.data.auth.LoginRepository
import com.noamrault.chatapp.databinding.FragmentGroupBinding


class GroupFragment : Fragment() {

    private var _binding: FragmentGroupBinding? = null
    private val binding: FragmentGroupBinding get() = _binding!!

    private val loginRepo: LoginRepository = LoginRepository(LoginDataSource())
    private val userId = loginRepo.user!!.uid
    private val serviceId = BuildConfig.APPLICATION_ID
    private val strategy = Strategy.P2P_STAR

    // Callbacks for receiving payloads
    private val payloadCallback: PayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {

        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {

        }
    }

    // Callbacks for connections to other devices
    val connectionLifecycleCallback: ConnectionLifecycleCallback =
        object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
                Log.i(ContentValues.TAG, "onConnectionInitiated: accepting connection")

                // Automatically accept the connection on both sides.
                // Nearby.getConnectionsClient(this@MainActivity).acceptConnection(endpointId, payloadCallback)

                AlertDialog.Builder(requireActivity())
                    .setTitle("Accept connection to " + info.endpointName)
                    .setMessage("Confirm the code matches on both devices: " + info.authenticationDigits)
                    .setPositiveButton(
                        R.string.dialog_accept
                    ) { _: DialogInterface?, _: Int ->  // The user confirmed, so we can accept the connection.
                        Nearby.getConnectionsClient(requireActivity())
                            .acceptConnection(endpointId, payloadCallback)
                    }
                    .setNegativeButton(
                        R.string.dialog_cancel
                    ) { _: DialogInterface?, _: Int ->  // The user canceled, so we should reject the connection.
                        Nearby.getConnectionsClient(requireActivity())
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

    // Callbacks for finding other devices
    private val endpointDiscoveryCallback: EndpointDiscoveryCallback =
        object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                Log.i(TAG, "onEndpointFound: endpoint found, connecting")
                Nearby.getConnectionsClient(requireActivity()).requestConnection(
                    userId,
                    endpointId,
                    connectionLifecycleCallback
                )

                val bytesPayload = Payload.fromBytes("test".toByteArray())
                Nearby.getConnectionsClient(requireContext()).sendPayload(endpointId, bytesPayload)
            }

            override fun onEndpointLost(endpointId: String) {

            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Show Options Menu
        setHasOptionsMenu(true)

        val groupId = arguments?.getString("groupId")
        (activity as MainActivity).setActionBarTitle(groupId)
    }

    /** Setup an Option Menu to Show the group members or to Quit the group */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_group_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onStart() {
        super.onStart()

        (activity as MainActivity).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        startDiscovery()
    }

    override fun onDestroy() {
        super.onDestroy()

        (activity as MainActivity).setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    private fun startDiscovery() {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(strategy).build()
        Nearby.getConnectionsClient(requireContext())
            .startDiscovery(serviceId, endpointDiscoveryCallback, discoveryOptions)
            .addOnSuccessListener { }
            .addOnFailureListener { e: java.lang.Exception? -> }
    }
}