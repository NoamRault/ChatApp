package com.noamrault.chatapp.data

import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(private val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    var user: FirebaseUser? = Firebase.auth.currentUser
        private set

    val isLoggedIn: Boolean
        get() = user != null

    fun logout() {
        dataSource.logout()
    }

    suspend fun register(
        email: String,
        username: String,
        password: String,
        fragment: Fragment
    ): Result<FirebaseUser> {
        return dataSource.register(email, username, password, fragment)
    }

    suspend fun login(email: String, password: String, fragment: Fragment): Result<FirebaseUser> {
        return dataSource.login(email, password, fragment)
    }
}