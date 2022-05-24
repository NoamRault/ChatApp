package com.noamrault.chatapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.noamrault.chatapp.R
import com.noamrault.chatapp.data.LoginDataSource
import com.noamrault.chatapp.data.LoginRepository
import com.noamrault.chatapp.databinding.FragmentMainBinding
import com.noamrault.chatapp.friendList.FriendAdapter
import com.noamrault.chatapp.groupList.GroupAdapter


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding get() = _binding!!

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
        _binding = FragmentMainBinding.inflate(inflater, container, false)

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
            Snackbar.make(view, "Create A New Group", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        fabAddFriend.setOnClickListener {
            AddFriendDialogFragment().show(childFragmentManager, AddFriendDialogFragment.TAG)
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

        showMessages()

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

    private fun showMessages() {
        fabNewGroup.show()
        fabAddFriend.hide()

        val groupList: ArrayList<String> = ArrayList()

        recyclerView.adapter = GroupAdapter(groupList)

        Toast.makeText(
            activity?.baseContext,
            "In development",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showFriends() {
        fabNewGroup.hide()
        fabAddFriend.show()

        loginRepo.user?.let {
            Firebase.firestore.collection("users").document(it.uid).get()
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        @Suppress("UNCHECKED_CAST") val friendList: ArrayList<String>? =
                            task.result.get("friends") as ArrayList<String>?
                        if (friendList == null) {
                            Toast.makeText(
                                activity?.baseContext,
                                "No friends found",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val friendMap: HashMap<String, String> = HashMap()
                            for (friend in friendList) {
                                Firebase.firestore.collection("users").document(friend).get()
                                    .addOnCompleteListener(requireActivity()) { task2 ->
                                        friendMap[friend] = task2.result.get("username") as String
                                        recyclerView.adapter = FriendAdapter(friendList, friendMap)
                                    }
                            }
                        }
                    } else {
                        Toast.makeText(
                            activity?.baseContext,
                            "Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}