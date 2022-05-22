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
import com.noamrault.chatapp.databinding.FragmentLoginBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LoginFragment : BaseFragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setProgressBar(binding.progressBar)

        // Buttons
        with(binding) {
            loginButton.setOnClickListener {
                val email = binding.emailAddress.text.toString()
                val password = binding.password.text.toString()
                MainScope().launch {
                    signIn(email, password)
                }
            }
            registerButton.setOnClickListener {
                view.findNavController().navigate(R.id.action_LoginFragment_to_RegisterFragment)
            }
        }
    }

    private suspend fun signIn(email: String, password: String) {
        Log.d(TAG, "signIn:$email")
        if (!validateForm()) {
            return
        }

        showProgressBar()
        val result = LoginRepository(LoginDataSource()).login(email, password, this)
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

        val password = binding.password.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.password.error = "Required."
            valid = false
        } else {
            binding.password.error = null
        }

        return valid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "LoginFragment"
    }
}