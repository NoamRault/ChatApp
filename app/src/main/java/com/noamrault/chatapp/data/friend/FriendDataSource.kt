package com.noamrault.chatapp.data.group

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.noamrault.chatapp.R
import com.noamrault.chatapp.data.friend.FriendAdapter
import kotlinx.coroutines.tasks.await

/**
 * Class that retrieves groups information.
 */
class FriendDataSource {

    companion object {
        suspend fun getFriends(
            uid: String,
            fragment: Fragment
        ): ArrayList<String> {
            val activity = fragment.requireActivity()
            var friendList: ArrayList<String> = ArrayList()

            Firebase.firestore.collection("users").document(uid).get()
                .addOnSuccessListener { result ->
                    if (result.get("friends") == null) {
                        Toast.makeText(
                            activity.baseContext,
                            "No friends found",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        @Suppress("UNCHECKED_CAST")
                        friendList = result.get("friends") as ArrayList<String>
                    }
                }
                .addOnFailureListener() {
                    Toast.makeText(
                        activity.baseContext,
                        "Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }.await()

            return friendList
        }

        suspend fun getFriendMap(
            friendList: ArrayList<String>
        ): HashMap<String, String> {
            val friendMap: HashMap<String, String> = HashMap()

            for (friend in friendList) {
                Firebase.firestore.collection("users").document(friend).get()
                    .addOnSuccessListener { result ->
                        friendMap[friend] = result.get("username") as String
                    }.await()
            }

            return friendMap
        }
    }
}