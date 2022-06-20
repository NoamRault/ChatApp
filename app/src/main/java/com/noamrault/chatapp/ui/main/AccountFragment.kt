package com.noamrault.chatapp.ui.main

import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.noamrault.chatapp.MainActivity
import com.noamrault.chatapp.R
import com.noamrault.chatapp.data.auth.LoginDataSource
import com.noamrault.chatapp.data.auth.LoginRepository
import com.noamrault.chatapp.databinding.FragmentAccountBinding


class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding: FragmentAccountBinding get() = _binding!!

    private val loginRepo: LoginRepository = LoginRepository(LoginDataSource())
    private val username get() = loginRepo.user!!.displayName
    private val email get() = loginRepo.user!!.email

    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView

    private lateinit var usernameButton: TextView
    private lateinit var emailButton: TextView
    private lateinit var passwordButton: TextView
    private lateinit var deleteButton: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)

        usernameTextView = binding.root.findViewById(R.id.fragment_account_username_text_view)
        emailTextView = binding.root.findViewById(R.id.fragment_account_email_text_view)

        usernameButton = binding.root.findViewById(R.id.fragment_account_username_text_view_list)
        emailButton = binding.root.findViewById(R.id.fragment_account_email_text_view_list)
        passwordButton = binding.root.findViewById(R.id.fragment_account_password_text_view_list)
        deleteButton = binding.root.findViewById(R.id.fragment_account_delete_text_view_list)

        usernameTextView.text = username
        emailTextView.text = email

        usernameButton.setOnClickListener {
            ChangeUsernameDialogFragment(this).show(childFragmentManager, TAG)
        }
        emailButton.setOnClickListener {
            ChangeEmailDialogFragment(this).show(childFragmentManager, TAG)
        }
        passwordButton.setOnClickListener {
            ChangePasswordDialogFragment().show(childFragmentManager, TAG)
        }
        deleteButton.setOnClickListener {
            DeleteAccountDialogFragment(this).show(childFragmentManager, TAG)
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        (activity as MainActivity).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    override fun onDestroy() {
        super.onDestroy()

        (activity as MainActivity).setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    fun updateUsername() {
        usernameTextView.text = username
        (activity as MainActivity).setNavHeaderName()
    }

    fun updateEmail() {
        emailTextView.text = email
        (activity as MainActivity).setNavHeaderName()
    }
}

class ChangeUsernameDialogFragment(private val accountFragment: AccountFragment) :
    DialogFragment() {
    private val loginRepo: LoginRepository = LoginRepository(LoginDataSource())

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val usernameInput = EditText(activity).apply {
            hint = "New Username"
        }

        return activity?.let { activity ->
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder(activity)
                .setMessage("Change Username")
                .setView(usernameInput)
                .setPositiveButton("Accept") { _, _ ->
                    changeUsername(usernameInput.text.toString())
                }
                .setNegativeButton(R.string.dialog_cancel) { _, _ ->
                    // User cancelled the dialog
                }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun changeUsername(username: String) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(username).build()
        loginRepo.user!!.updateProfile(profileUpdates).addOnCompleteListener {
            accountFragment.updateUsername()
        }

        Firebase.firestore
            .collection("users")
            .document(loginRepo.user!!.uid)
            .update("username", username)
    }
}

class ChangeEmailDialogFragment(private val accountFragment: AccountFragment) : DialogFragment() {
    private val loginRepo: LoginRepository = LoginRepository(LoginDataSource())

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL

        val emailInput = EditText(activity).apply {
            hint = "New Email Address"
            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
        val passwordInput = EditText(activity).apply {
            hint = "Password"
            transformationMethod = PasswordTransformationMethod.getInstance()
        }

        layout.apply {
            addView(emailInput)
            addView(passwordInput)
        }

        return activity?.let { activity ->
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder(activity)
                .setMessage("Change Email")
                .setView(layout)
                .setPositiveButton("Accept") { _, _ ->
                    changeEmail(
                        emailInput.text.toString(),
                        passwordInput.text.toString()
                    )
                }
                .setNegativeButton(R.string.dialog_cancel) { _, _ ->
                    // User cancelled the dialog
                }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun changeEmail(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(loginRepo.user!!.email!!, password)
        Firebase.auth.currentUser!!.reauthenticate(credential).addOnCompleteListener {
            loginRepo.user!!.updateEmail(email).addOnCompleteListener {
                accountFragment.updateEmail()
            }
        }
    }
}

class ChangePasswordDialogFragment : DialogFragment() {
    private val loginRepo: LoginRepository = LoginRepository(LoginDataSource())

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL

        val oldPasswordInput = EditText(activity).apply {
            hint = "Old Password"
            transformationMethod = PasswordTransformationMethod.getInstance()
        }
        val newPasswordInput = EditText(activity).apply {
            hint = "New Password"
            transformationMethod = PasswordTransformationMethod.getInstance()
        }
        val confirmPasswordInput = EditText(activity).apply {
            hint = "Confirm New Password"
            transformationMethod = PasswordTransformationMethod.getInstance()
        }

        layout.apply {
            addView(oldPasswordInput)
            addView(newPasswordInput)
            addView(confirmPasswordInput)
        }

        return activity?.let { activity ->
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder(activity)
                .setMessage("Change Password")
                .setView(layout)
                .setPositiveButton("Accept") { _, _ ->
                    changePassword(
                        oldPasswordInput.text.toString(),
                        newPasswordInput.text.toString(),
                        confirmPasswordInput.text.toString()
                    )
                }
                .setNegativeButton(R.string.dialog_cancel) { _, _ ->
                    // User cancelled the dialog
                }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ) {
        if (newPassword == confirmNewPassword) {
            val credential = EmailAuthProvider.getCredential(loginRepo.user!!.email!!, oldPassword)
            Firebase.auth.currentUser!!.reauthenticate(credential).addOnCompleteListener {
                loginRepo.user!!.updatePassword(newPassword)
            }
        }
    }

}

class DeleteAccountDialogFragment(private val accountFragment: AccountFragment) : DialogFragment() {
    private val loginRepo: LoginRepository = LoginRepository(LoginDataSource())

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val passwordInput = EditText(activity).apply {
            hint = "Password"
            transformationMethod = PasswordTransformationMethod.getInstance()
        }

        return activity?.let { activity ->
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder(activity)
                .setMessage("Delete Account")
                .setView(passwordInput)
                .setPositiveButton("Delete") { _, _ ->
                    deleteAccount(passwordInput.text.toString())
                }
                .setNegativeButton(R.string.dialog_cancel) { _, _ ->
                    // User cancelled the dialog
                }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun deleteAccount(password: String) {
        val credential = EmailAuthProvider.getCredential(loginRepo.user!!.email!!, password)
        Firebase.auth.currentUser!!.reauthenticate(credential).addOnCompleteListener {
            Firebase.auth.currentUser!!.delete().addOnSuccessListener {
                loginRepo.logout()
                (accountFragment.activity as MainActivity).authenticate()
            }
        }
    }
}