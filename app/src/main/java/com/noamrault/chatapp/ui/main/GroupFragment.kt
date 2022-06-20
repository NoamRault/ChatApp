package com.noamrault.chatapp.ui.main

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.multidex.BuildConfig
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.noamrault.chatapp.MainActivity
import com.noamrault.chatapp.R
import com.noamrault.chatapp.data.ObjectSerializer
import com.noamrault.chatapp.data.SharedHelper
import com.noamrault.chatapp.data.auth.LoginDataSource
import com.noamrault.chatapp.data.auth.LoginRepository
import com.noamrault.chatapp.data.message.Message
import com.noamrault.chatapp.data.message.MessageAdapter
import com.noamrault.chatapp.data.message.MessageDataSource
import com.noamrault.chatapp.databinding.FragmentGroupBinding
import java.util.*

class GroupFragment : Fragment() {

    private var _binding: FragmentGroupBinding? = null
    private val binding: FragmentGroupBinding get() = _binding!!

    private val loginRepo: LoginRepository = LoginRepository(LoginDataSource())
    private val userId = loginRepo.user!!.uid

    private lateinit var groupId: String
    private var groupName: String? = null
    private lateinit var messageEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var sendButton: ImageButton

    private val serviceId = BuildConfig.APPLICATION_ID
    private val strategy = Strategy.P2P_STAR

    /** Callbacks for finding other devices */
    private val endpointDiscoveryCallback: EndpointDiscoveryCallback =
        object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                Log.i(TAG, "onEndpointFound: endpoint found, connecting")
                Nearby.getConnectionsClient(requireActivity()).requestConnection(
                    loginRepo.user!!.displayName!!,
                    endpointId,
                    (activity as MainActivity).connectionLifecycleCallback
                )
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
        recyclerView = binding.root.findViewById(R.id.fragment_group_recycler_view)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity).apply {
                orientation = LinearLayoutManager.VERTICAL
                reverseLayout = false
                stackFromEnd = false
            }
        }

        sendButton.setOnClickListener {
            sendMessages()
        }

        showMessages()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Show Options Menu
        setHasOptionsMenu(true)

        groupId = arguments?.getString("groupId")!!
        groupName = arguments?.getString("groupName")!!
        if(groupName != null) {
            (activity as MainActivity).setActionBarTitle(groupName)
        }
    }

    /** Setup an Option Menu to Show the group members or to Quit the group */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_group_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

        val bundle = bundleOf("groupId" to groupId)

        menu.findItem(R.id.menu_add_members).setOnMenuItemClickListener {
            view?.findNavController()?.navigate(R.id.action_group_to_add_members, bundle)
            true
        }
        menu.findItem(R.id.menu_show_members).setOnMenuItemClickListener {
            view?.findNavController()?.navigate(R.id.action_group_to_show_members, bundle)
            true
        }
        menu.findItem(R.id.menu_quit_group).setOnMenuItemClickListener {
            LeaveGroupDialogFragment(groupId).show(childFragmentManager, AddFriendDialogFragment.TAG)
            true
        }
    }

    override fun onStart() {
        super.onStart()

        (activity as MainActivity).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        if(groupName != null) {
            (activity as MainActivity).setActionBarTitle(groupName)
        }

        startDiscovery()
    }

    fun showMessages() {
        val messageList = MessageDataSource.getMessages(groupId, this)
        val messageAdapter = MessageAdapter(messageList, requireActivity() as MainActivity)

        recyclerView.adapter = messageAdapter
        recyclerView.scrollToPosition(messageAdapter.itemCount - 1)
    }

    private fun sendMessages() {
        if (messageEditText.text.toString() != "") {
            if ((activity as MainActivity).endpointIds.isNotEmpty()) {
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

                for (endpointId in (activity as MainActivity).endpointIds) {
                    Nearby.getConnectionsClient(requireContext())
                        .sendPayload(endpointId, bytesPayload)
                }

                (activity as MainActivity).database.messageDao().insert(message)
                showMessages()
                messageEditText.setText("")

                Toast.makeText(
                    requireActivity().baseContext,
                    getString(R.string.message_sent),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireActivity().baseContext,
                    getString(R.string.fragment_group_not_connected),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        Nearby.getConnectionsClient(requireContext()).stopDiscovery()
        (activity as MainActivity).setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    private fun startDiscovery() {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(strategy).build()
        Nearby.getConnectionsClient(requireContext())
            .startDiscovery(serviceId, endpointDiscoveryCallback, discoveryOptions)
    }
}