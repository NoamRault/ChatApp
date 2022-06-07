package com.noamrault.chatapp.data.group

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.noamrault.chatapp.R
import kotlinx.coroutines.tasks.await

/**
 * Class that retrieves groups information.
 */
class GroupDataSource {

    companion object {
        suspend fun getGroups(
            uid: String,
            fragment: Fragment
        ): ArrayList<String> {
            val groupList: ArrayList<String> = ArrayList()

            Firebase.firestore.collection("group")
                .whereArrayContains("members", uid)
                .get()
                .addOnSuccessListener { documents ->
                    if(documents.isEmpty) {
                        Toast.makeText(
                            fragment.requireActivity().baseContext,
                            R.string.no_group_found,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else {
                        for (document in documents) {
                            groupList.add(document.id)
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        fragment.requireActivity().baseContext,
                        "Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }.await()

            return groupList
        }
    }
}