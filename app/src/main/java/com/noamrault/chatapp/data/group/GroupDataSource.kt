package com.noamrault.chatapp.data.group

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.noamrault.chatapp.MainActivity
import com.noamrault.chatapp.R
import com.noamrault.chatapp.data.friend.Friend
import kotlinx.coroutines.tasks.await

/**
 * Class that retrieves groups information.
 */
class GroupDataSource {

    companion object {
        fun getGroups(
            fragment: Fragment
        ): ArrayList<Group> {
            return ArrayList(
                (fragment.requireActivity() as MainActivity)
                    .database
                    .groupDao()
                    .getAll()
            )
        }

        suspend fun getGroupsFromServer(
            uid: String,
            activity: Activity
        ) {
            val groupIdList: ArrayList<String> = ArrayList()
            val groupList: ArrayList<Group> = ArrayList()

            // Get all the Groups where the user is a member
            Firebase.firestore.collection("group")
                .whereArrayContains("members", uid)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Toast.makeText(
                            activity.baseContext,
                            R.string.no_group_found,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        for (document in documents) {
                            groupIdList.add(document.id)
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        activity.baseContext,
                        "Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }.await()

            if (groupIdList.isNotEmpty()) {
                for (groupId in groupIdList) {
                    var groupName = ""
                    val groupMembers: ArrayList<Friend> = ArrayList()
                    var groupMembersIds: List<String> = ArrayList()

                    // Get the IDs of each group member
                    Firebase.firestore.collection("group")
                        .document(groupId)
                        .get()
                        .addOnSuccessListener { result ->
                            groupName = result.get("name") as String
                            @Suppress("UNCHECKED_CAST")
                            groupMembersIds = result.get("members") as List<String>
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                activity.baseContext,
                                "Failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }.await()

                    for (memberId in groupMembersIds) {
                        // Get the names of each group member
                        Firebase.firestore.collection("users")
                            .document(memberId)
                            .get()
                            .addOnSuccessListener { result ->
                                groupMembers.add(Friend(memberId, result.get("username") as String))
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    activity.baseContext,
                                    "Failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }.await()
                    }

                    val group = Group(groupId, groupName, groupMembers)
                    groupList.add(group)
                }

                (activity as MainActivity)
                    .database
                    .groupDao().apply {
                        deleteAll()
                        insertAll(groupList)
                    }
            }
        }
    }
}