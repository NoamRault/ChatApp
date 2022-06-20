package com.noamrault.chatapp.ui.main

import android.app.Activity
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

class AddFriendDialogFragment(private val homeFragment: HomeFragment) : DialogFragment() {

    private val loginRepo: LoginRepository = LoginRepository(LoginDataSource())

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val usernameInput = EditText(activity).apply {
            hint = getString(R.string.dialog_add_friend_edittext_hint)
        }

        return activity?.let { activity ->
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder(activity)
                .setMessage(R.string.dialog_add_friend_title)
                .setView(usernameInput)
                .setPositiveButton(R.string.dialog_add_friend_accept) { _, _ ->
                    addFriend(usernameInput, activity)
                }
                .setNegativeButton(R.string.dialog_cancel) { _, _ ->
                    // User cancelled the dialog
                }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun addFriend(usernameInput: EditText, activity: Activity) {
        loginRepo.user?.let { user ->
            Firebase.firestore.collection("users")
                .whereEqualTo("username", usernameInput.text.toString())
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Toast.makeText(
                            activity,
                            R.string.dialog_add_friend_not_found,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        for (document in documents) {
                            Firebase.firestore
                                .collection("users")
                                .document(user.uid)
                                .update(
                                    "friends",
                                    FieldValue.arrayUnion(document.id)
                                )
                        }
                        Toast.makeText(
                            activity,
                            R.string.dialog_add_friend_success,
                            Toast.LENGTH_SHORT
                        ).show()
                        MainScope().launch {
                            (activity as MainActivity).refreshFriends()
                            homeFragment.showFriends()
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        activity,
                        "Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    companion object {
        const val TAG = "AddFriendDialog"
    }
}