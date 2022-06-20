package com.noamrault.chatapp.data.friend

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.noamrault.chatapp.MainActivity
import kotlinx.coroutines.tasks.await

/**
 * Class that retrieves groups information.
 */
class FriendDataSource {

    companion object {
        fun getFriends(
            fragment: Fragment
        ): ArrayList<Friend> {
            return ArrayList(
                (fragment.requireActivity() as MainActivity)
                    .database
                    .friendDao()
                    .getAll()
            )
        }

        suspend fun getFriendsFromServer(
            uid: String,
            activity: Activity
        ) {
            var friendIdList: ArrayList<String>? = ArrayList()
            val friendList: ArrayList<Friend> = ArrayList()

            // Get all user's Friends
            Firebase.firestore.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { result ->
                    if (result.get("friends") == null) {
                        Toast.makeText(
                            activity.baseContext,
                            "No friends found",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        @Suppress("UNCHECKED_CAST")
                        friendIdList = result.get("friends") as ArrayList<String>
                    }
                }
                .addOnFailureListener() {
                    friendIdList = null
                    Toast.makeText(
                        activity.baseContext,
                        "Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }.await()

            if (friendIdList != null) {
                for (friendId in friendIdList!!) {
                    // Get the usernames of each friend
                    Firebase.firestore.collection("users")
                        .document(friendId)
                        .get()
                        .addOnSuccessListener { result ->
                            friendList.add(Friend(friendId, result.get("username") as String))
                        }.await()
                }

                (activity as MainActivity)
                    .database
                    .friendDao().apply {
                        deleteAll()
                        insertAll(friendList)
                    }
            }
        }
    }
}