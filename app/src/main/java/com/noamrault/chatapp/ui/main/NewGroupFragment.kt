package com.noamrault.chatapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.noamrault.chatapp.MainActivity
import com.noamrault.chatapp.R
import com.noamrault.chatapp.data.auth.LoginDataSource
import com.noamrault.chatapp.data.auth.LoginRepository
import com.noamrault.chatapp.data.friend.FriendDataSource
import com.noamrault.chatapp.data.newGroup.NewGroupAdapter
import com.noamrault.chatapp.data.newGroup.NewGroupModel
import com.noamrault.chatapp.databinding.FragmentNewGroupBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


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
                selectedFriends.add(model.getId())
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

        MainScope().launch {
            (activity as MainActivity).refreshGroups()
            activity?.onBackPressed()
        }
    }

    private fun showFriends() {
        val friendList = FriendDataSource.getFriends(this)
        val modelList = ArrayList<NewGroupModel>()

        for (friend in friendList) {
            modelList.add(NewGroupModel(friend))
        }

        recyclerView.adapter = NewGroupAdapter(modelList)
    }


}