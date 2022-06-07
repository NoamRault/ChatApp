package com.noamrault.chatapp.ui.main

import android.content.ContentValues.TAG
import android.graphics.ColorSpace.Model
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.noamrault.chatapp.R
import com.noamrault.chatapp.data.SharedHelper
import com.noamrault.chatapp.data.auth.LoginDataSource
import com.noamrault.chatapp.data.auth.LoginRepository
import com.noamrault.chatapp.databinding.FragmentNewGroupBinding
import com.noamrault.chatapp.data.friend.FriendAdapter
import com.noamrault.chatapp.data.group.GroupAdapter
import com.noamrault.chatapp.data.newGroup.NewGroupAdapter
import com.noamrault.chatapp.data.newGroup.NewGroupModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class NewGroupFragment : Fragment() {

    private var _binding: FragmentNewGroupBinding? = null
    private val binding: FragmentNewGroupBinding get() = _binding!!

    private val loginRepo: LoginRepository = LoginRepository(LoginDataSource())
    private lateinit var fabCreateGroup: FloatingActionButton
    private lateinit var groupNameEdit: EditText
    private lateinit var recyclerView: RecyclerView

    private val modelList: ArrayList<NewGroupModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewGroupBinding.inflate(inflater, container, false)

        fabCreateGroup = binding.root.findViewById(R.id.fab_create_group)
        groupNameEdit = binding.root.findViewById(R.id.fragment_new_group_name)
        recyclerView = binding.root.findViewById(R.id.fragment_new_group_recycler_view)

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

        fabCreateGroup.setOnClickListener {
            if (groupNameEdit.text.toString() != "") {
                createGroup(groupNameEdit.text.toString())
            }
        }

        return binding.root
    }

    private fun createGroup(groupName : String) {
        val selectedFriends = ArrayList<String>()
        selectedFriends.add(loginRepo.user!!.uid)

        for (model in modelList) {
            if (model.isSelected()) {
                selectedFriends.add(model.getText())
            }
        }

        val groupMap = HashMap<String, Any>()
        groupMap["name"] = groupName
        groupMap["members"] = selectedFriends

        Firebase.firestore
            .collection("group")
            .add(
                groupMap
            )

        activity?.onBackPressed()
    }

    private fun showFriends() {
        var friendList: ArrayList<String>
        Firebase.firestore.collection("users").document(loginRepo.user!!.uid).get()
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    if (task.result.get("friends") == null) {
                        Toast.makeText(
                            activity?.baseContext,
                            "No friends found",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        @Suppress("UNCHECKED_CAST")
                        friendList = task.result.get("friends") as ArrayList<String>
                        val friendMap: HashMap<String, String> = HashMap()
                        for (friend in friendList) {
                            Firebase.firestore.collection("users").document(friend).get()
                                .addOnCompleteListener(requireActivity()) { task2 ->
                                    friendMap[friend] = task2.result.get("username") as String
                                    modelList.add(NewGroupModel(friend))
                                    recyclerView.adapter = NewGroupAdapter(modelList, friendMap)
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