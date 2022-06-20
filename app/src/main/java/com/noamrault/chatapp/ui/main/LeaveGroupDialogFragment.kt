package com.noamrault.chatapp.ui.main

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
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

class LeaveGroupDialogFragment(private val groupId: String) : DialogFragment() {

    private val loginRepo: LoginRepository = LoginRepository(LoginDataSource())

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder(activity)
                .setMessage("Leave group?")
                .setPositiveButton("Leave") { _, _ ->
                    leaveGroup(activity)
                }
                .setNegativeButton(R.string.dialog_cancel) { _, _ ->
                    // User cancelled the dialog
                }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun leaveGroup(activity: Activity) {
        loginRepo.user?.let { user ->
            Firebase.firestore.collection("group").document(groupId)
                .update("members", FieldValue.arrayRemove(user.uid))
                .addOnSuccessListener {
                    Toast.makeText(
                        activity,
                        "Group Left",
                        Toast.LENGTH_SHORT
                    ).show()
                    MainScope().launch {
                        (activity as MainActivity).refreshGroups()
                        activity.onBackPressed()
                    }
                }
        }
    }

}