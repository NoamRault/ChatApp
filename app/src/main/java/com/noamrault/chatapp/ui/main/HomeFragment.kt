package com.noamrault.chatapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.noamrault.chatapp.R
import com.noamrault.chatapp.data.friend.FriendAdapter
import com.noamrault.chatapp.data.friend.FriendDataSource
import com.noamrault.chatapp.data.group.GroupAdapter
import com.noamrault.chatapp.data.group.GroupDataSource
import com.noamrault.chatapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = _binding!!

    private var backPressTime: Long = 0
    private lateinit var fabNewGroup: FloatingActionButton
    private lateinit var fabAddFriend: FloatingActionButton
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val tabLayout = binding.root.findViewById<TabLayout>(R.id.fragment_main_tabs)
        fabNewGroup = binding.root.findViewById(R.id.fab_new_group)
        fabAddFriend = binding.root.findViewById(R.id.fab_add_friend)
        recyclerView = binding.root.findViewById(R.id.fragment_main_recycler_view)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(
                activity,
                LinearLayoutManager.VERTICAL,
                false
            )
            addItemDecoration(
                DividerItemDecoration(
                    this.context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        fabNewGroup.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_home_to_new_group)
        }
        fabAddFriend.setOnClickListener {
            AddFriendDialogFragment(this).show(childFragmentManager, AddFriendDialogFragment.TAG)
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Handle tab select
                if (tab != null) {
                    when (tab.text) {
                        getString(R.string.tab_messages) -> showMessages()
                        getString(R.string.tab_friends) -> showFriends()
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselect
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselect
            }
        })

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressTime + 2000 > System.currentTimeMillis()) {
                    activity?.finish()
                } else {
                    Toast.makeText(
                        activity?.baseContext,
                        R.string.back_to_exit,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                backPressTime = System.currentTimeMillis()
            }
        })
    }

    override fun onResume() {
        super.onResume()

        showMessages()
    }

    fun openGroup(groupId: String, groupName: String) {
        val bundle = bundleOf("groupId" to groupId, "groupName" to groupName)
        view?.findNavController()?.navigate(R.id.action_home_to_group, bundle)
    }

    fun showMessages() {
        fabNewGroup.show()
        fabAddFriend.hide()

        val groupList = GroupDataSource.getGroups(this)

        recyclerView.adapter = GroupAdapter(groupList)
    }

    fun showFriends() {
        fabNewGroup.hide()
        fabAddFriend.show()

        val friendList = FriendDataSource.getFriends(this)

        recyclerView.adapter = FriendAdapter(friendList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}