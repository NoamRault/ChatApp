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
import com.noamrault.chatapp.data.friend.FriendDataSource
import com.noamrault.chatapp.data.newGroup.NewGroupAdapter
import com.noamrault.chatapp.data.newGroup.NewGroupModel
import com.noamrault.chatapp.databinding.FragmentGroupAddMembersBinding
import com.noamrault.chatapp.databinding.FragmentGroupMembersBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AddMembersFragment : Fragment() {

    private var _binding: FragmentGroupAddMembersBinding? = null
    private val binding: FragmentGroupAddMembersBinding get() = _binding!!

    private lateinit var groupId: String

    private lateinit var fabAddMembers: FloatingActionButton
    private lateinit var recyclerView: RecyclerView

    private val modelList: ArrayList<NewGroupModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupAddMembersBinding.inflate(inflater, container, false)

        fabAddMembers = binding.root.findViewById(R.id.fab_group_add_members)
        recyclerView = binding.root.findViewById(R.id.fragment_group_add_members_recycler_view)

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

        showFriends()

        fabAddMembers.setOnClickListener {
            addMembers()
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        groupId = arguments?.getString("groupId")!!
    }

    private fun addMembers() {
        for (model in modelList) {
            if (model.isSelected()) {
                Firebase.firestore
                    .collection("group")
                    .document(groupId)
                    .update(
                        "members",
                        FieldValue.arrayUnion(model.getId())
                    )
            }
        }

        MainScope().launch {
            (activity as MainActivity).refreshGroups()
            activity?.onBackPressed()
        }
    }

    private fun showFriends() {
        val friendList = FriendDataSource.getFriends(this)

        for (friend in friendList) {
            modelList.add(NewGroupModel(friend))
        }

        recyclerView.adapter = NewGroupAdapter(modelList)
    }
}