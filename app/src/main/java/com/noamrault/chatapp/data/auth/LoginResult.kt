package com.noamrault.chatapp.data.auth

import com.google.firebase.auth.FirebaseUser

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class LoginResult<out T : Any> {

    data class Success<out T : Any>(val data: FirebaseUser?) : LoginResult<T>()
    data class Error(val exception: Exception) : LoginResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }
}