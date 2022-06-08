package com.noamrault.chatapp.ui.main

import android.app.AlertDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.multidex.BuildConfig
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.noamrault.chatapp.MainActivity
import com.noamrault.chatapp.R
import com.noamrault.chatapp.data.ObjectSerializer
import com.noamrault.chatapp.data.SharedHelper
import com.noamrault.chatapp.data.auth.LoginDataSource
import com.noamrault.chatapp.data.auth.LoginRepository
import com.noamrault.chatapp.data.group.GroupAdapter
import com.noamrault.chatapp.data.group.GroupDataSource
import com.noamrault.chatapp.data.message.Message
import com.noamrault.chatapp.data.message.MessageAdapter
import com.noamrault.chatapp.data.message.MessageDao
import com.noamrault.chatapp.data.message.MessageDataSource
import com.noamrault.chatapp.databinding.FragmentGroupBinding
import java.time.Instant
import java.util.*


class GroupFragment : Fragment() {

    private var _binding: FragmentGroupBinding? = null
    private val binding: FragmentGroupBinding get() = _binding!!

    private val loginRepo: LoginRepository = LoginRepository(LoginDataSource())
    private val userId = loginRepo.user!!.uid

    private lateinit var groupId: String
    private lateinit var messageEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var sendButton: ImageButton

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
                Log.i(TAG, "onConnectionInitiated: accepting connection")

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
                Log.i(TAG, "onDisconnected: disconnected from the opponent")
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

                var messageId = SharedHelper.getRandomString()

                while (
                    messageId in (activity as MainActivity).database.messageDao()
                        .findIdByGroup(groupId)
                ) {
                    messageId = SharedHelper.getRandomString()
                }

                val message = Message(
                    messageId,
                    groupId,
                    messageEditText.text.toString(),
                    Calendar.getInstance().time,
                    userId
                )

                val serializedMessage = ObjectSerializer.serialize(message)

                val bytesPayload = Payload.fromBytes(serializedMessage.toByteArray())
                Nearby.getConnectionsClient(requireContext()).sendPayload(endpointId, bytesPayload)
                    .addOnSuccessListener {
                        (activity as MainActivity).database.messageDao().insertAll(message)
                        showMessages()
                    }
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

        messageEditText = binding.root.findViewById(R.id.fragment_group_message_edittext)
        sendButton = binding.root.findViewById(R.id.fragment_group_send_button)
        recyclerView = binding.root.findViewById(R.id.fragment_main_recycler_view)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(
                activity,
                LinearLayoutManager.VERTICAL,
                true
            )
        }

        sendButton.setOnClickListener {
            startDiscovery()
        }

        showMessages()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Show Options Menu
        setHasOptionsMenu(true)

        groupId = arguments?.getString("groupId")!!
        val groupName = arguments?.getString("groupName")!!
        (activity as MainActivity).setActionBarTitle(groupName)
    }

    /** Setup an Option Menu to Show the group members or to Quit the group */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_group_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onStart() {
        super.onStart()

        (activity as MainActivity).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun showMessages() {
        val messageList = MessageDataSource.getMessages(userId, this)

        recyclerView.adapter = MessageAdapter(messageList)
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