package com.noamrault.chatapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.noamrault.chatapp.MainActivity
import com.noamrault.chatapp.R
import com.noamrault.chatapp.data.auth.LoginDataSource
import com.noamrault.chatapp.data.auth.LoginRepository
import com.noamrault.chatapp.data.friend.FriendAdapter
import com.noamrault.chatapp.data.friend.FriendDataSource
import com.noamrault.chatapp.data.group.GroupDataSource
import com.noamrault.chatapp.data.groupMembers.GroupMembersAdapter
import com.noamrault.chatapp.data.newGroup.NewGroupAdapter
import com.noamrault.chatapp.data.newGroup.NewGroupModel
import com.noamrault.chatapp.databinding.FragmentGroupMembersBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class GroupMembersFragment : Fragment()  {

    private var _binding: FragmentGroupMembersBinding? = null
    private val binding: FragmentGroupMembersBinding get() = _binding!!

    private lateinit var groupId: String

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupMembersBinding.inflate(inflater, container, false)

        recyclerView = binding.root.findViewById(R.id.fragment_group_members_recycler_view)

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

        showMembers()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        groupId = arguments?.getString("groupId")!!
    }

    fun showMembers() {
        val membersList = GroupDataSource.getGroupMembers(this, groupId)

        recyclerView.adapter = GroupMembersAdapter(membersList, groupId)
    }
}