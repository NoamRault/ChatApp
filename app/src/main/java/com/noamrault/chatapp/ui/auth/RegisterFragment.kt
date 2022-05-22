package com.noamrault.chatapp.ui.auth

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.noamrault.chatapp.R
import com.noamrault.chatapp.data.LoginDataSource
import com.noamrault.chatapp.data.LoginRepository
import com.noamrault.chatapp.data.Result
import com.noamrault.chatapp.databinding.FragmentRegisterBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class RegisterFragment : BaseFragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding: FragmentRegisterBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setProgressBar(binding.progressBar)

        // Buttons
        with(binding) {
            registerButton.setOnClickListener {
                val email = binding.emailAddress.text.toString()
                val username = binding.username.text.toString()
                val password = binding.password.text.toString()
                MainScope().launch {
                    createAccount(email, password, username)
                }
            }
            loginButton.setOnClickListener {
                view.findNavController().navigate(R.id.action_RegisterFragment_to_LoginFragment)
            }
        }
    }

    private suspend fun createAccount(email: String, password: String, username: String) {
        Log.d(TAG, "createAccount:$email")
        if (!validateForm()) {
            return
        }

        showProgressBar()
        val result = LoginRepository(LoginDataSource()).register(email, username, password, this)
        hideProgressBar()

        Log.d(TAG, "result:$result")

        if (result is Result.Success) {
            activity?.finish()
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = binding.emailAddress.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.emailAddress.error = "Required."
            valid = false
        } else {
            binding.emailAddress.error = null
        }

        val username = binding.username.text.toString()
        if (TextUtils.isEmpty(username)) {
            binding.username.error = "Required."
            valid = false
        } else {
            binding.username.error = null
        }

        val password = binding.password.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.password.error = "Required."
            valid = false
        } else {
            binding.password.error = null
        }

        val confirmPassword = binding.confirmPassword.text.toString()
        if (TextUtils.isEmpty(confirmPassword)) {
            binding.confirmPassword.error = "Required."
            valid = false
        } else if (password != confirmPassword) {
            binding.confirmPassword.error = "Passwords do not match."
            valid = false
        } else {
            binding.confirmPassword.error = null
        }

        return valid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "Auth"
        private const val RC_MULTI_FACTOR = 9005
    }
}