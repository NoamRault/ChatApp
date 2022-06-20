package com.noamrault.chatapp.ui.main

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.noamrault.chatapp.MainActivity
import com.noamrault.chatapp.R
import com.noamrault.chatapp.data.auth.LoginDataSource
import com.noamrault.chatapp.data.auth.LoginRepository
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class RemoveMemberDialogFragment(
    private val fragment: GroupMembersFragment,
    private val groupId: String,
    private val id: String,
    private val username: String?
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let { activity ->
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder(activity)
                .setMessage(getString(R.string.dialog_remove_member_title, username))
                .setPositiveButton(R.string.dialog_remove_friend_accept) { _, _ ->
                    Firebase.firestore.collection("group").document(groupId)
                        .update("members", FieldValue.arrayRemove(id))
                        .addOnSuccessListener {
                            Toast.makeText(
                                activity,
                                "Member Removed",
                                Toast.LENGTH_SHORT
                            ).show()
                            MainScope().launch {
                                (activity as MainActivity).refreshGroups()
                                fragment.showMembers()
                            }
                        }
                }
                .setNegativeButton(R.string.dialog_cancel) { _, _ ->
                    // User cancelled the dialog
                }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        const val TAG = "AddFriendDialog"
    }
}