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
import com.noamrault.chatapp.R
import com.noamrault.chatapp.data.LoginDataSource
import com.noamrault.chatapp.data.LoginRepository

class RemoveFriendDialogFragment(
    private val id: String,
    private val username: String?
) : DialogFragment() {

    private val loginRepo: LoginRepository = LoginRepository(LoginDataSource())

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder
                .setMessage(getString(R.string.dialog_remove_friend_title, username))
                .setPositiveButton(R.string.dialog_remove_friend_accept) { _, _ ->
                    loginRepo.user?.let { user ->
                        Firebase.firestore.collection("users")
                            .document(loginRepo.user!!.uid)
                            .get()
                            .addOnSuccessListener {
                                Firebase.firestore
                                    .collection("users")
                                    .document(user.uid)
                                    .update(
                                        "friends",
                                        FieldValue.arrayRemove(id)
                                    )
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    requireActivity().baseContext,
                                    R.string.dialog_remove_friend_not_found,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
                .setNegativeButton(R.string.dialog_cancel) { _, _ ->
                    // User cancelled the dialog
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        const val TAG = "AddFriendDialog"
    }
}