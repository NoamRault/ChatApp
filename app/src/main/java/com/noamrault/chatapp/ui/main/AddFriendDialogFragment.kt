package com.noamrault.chatapp.ui.main

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.noamrault.chatapp.R
import com.noamrault.chatapp.data.LoginDataSource
import com.noamrault.chatapp.data.LoginRepository

class AddFriendDialogFragment : DialogFragment() {

    private val loginRepo: LoginRepository = LoginRepository(LoginDataSource())

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val usernameInput = EditText(activity)

        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder
                .setMessage(R.string.dialog_title_send_friend_request)
                .setView(usernameInput)
                .setPositiveButton(R.string.dialog_accept_send_friend_request) { _, _ ->
                    loginRepo.user?.let { user ->
                        Firebase.firestore.collection("users")
                            .whereEqualTo("username", usernameInput.text.toString())
                            .get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {
                                    Firebase.firestore
                                        .collection("users")
                                        .document(user.uid)
                                        .update(
                                            "friends",
                                            FieldValue.arrayUnion(document.id)
                                        )
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    requireActivity().baseContext,
                                    R.string.dialog_send_friend_request_not_found,
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