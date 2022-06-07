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
import com.noamrault.chatapp.data.auth.LoginDataSource
import com.noamrault.chatapp.data.auth.LoginRepository
import com.noamrault.chatapp.databinding.FragmentHomeBinding
import com.noamrault.chatapp.data.friend.FriendAdapter
import com.noamrault.chatapp.data.group.FriendDataSource
import com.noamrault.chatapp.data.group.GroupAdapter
import com.noamrault.chatapp.data.group.GroupDataSource
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = _binding!!

    private var backPressTime: Long = 0
    private val loginRepo: LoginRepository = LoginRepository(LoginDataSource())
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
                        getString(R.string.tab_messages) -> MainScope().launch { showMessages() }
                        getString(R.string.tab_friends) -> MainScope().launch { showFriends() }
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

        MainScope().launch {
            showMessages()
        }

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

    fun openGroup(groupId: String) {
        val bundle = bundleOf("groupId" to groupId)
        view?.findNavController()?.navigate(R.id.action_home_to_group, bundle)
    }

    private suspend fun showMessages() {
        fabNewGroup.show()
        fabAddFriend.hide()

        val groupList = GroupDataSource.getGroups(loginRepo.user!!.uid, this)

        recyclerView.adapter = GroupAdapter(groupList)
    }

    suspend fun showFriends() {
        fabNewGroup.hide()
        fabAddFriend.show()

        val friendList = FriendDataSource.getFriends(loginRepo.user!!.uid, this)
        val friendMap = FriendDataSource.getFriendMap(friendList)

        recyclerView.adapter = FriendAdapter(friendList, friendMap)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}