package com.noamrault.chatapp.data.auth

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.noamrault.chatapp.R
import kotlinx.coroutines.tasks.await
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    private var auth: FirebaseAuth = Firebase.auth

    suspend fun register(
        email: String,
        username: String,
        password: String,
        fragment: Fragment
    ): LoginResult<FirebaseUser> {
        return try {
            var userFound = false

            Firebase.firestore.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        userFound = true
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
                .await()

            if (!userFound) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(fragment.requireActivity()) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success")

                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(username).build()
                            auth.currentUser?.updateProfile(profileUpdates)

                            val map = hashMapOf(
                                "username" to username
                            )
                            auth.currentUser?.let {
                                Firebase.firestore
                                    .collection("users")
                                    .document(it.uid)
                                    .set(map)
                            }

                            if (auth.currentUser?.displayName != null) {
                                Toast.makeText(
                                    fragment.context,
                                    fragment.context?.getString(
                                        R.string.login_success,
                                        auth.currentUser!!.displayName!!
                                    ),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                fragment.context, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .await()
            }

            if (auth.currentUser != null) {
                LoginResult.Success(auth.currentUser)
            } else {
                LoginResult.Error(IOException("Error registering"))
            }
        } catch (e: Throwable) {
            LoginResult.Error(IOException("Error registering", e))
        }
    }

    suspend fun login(email: String, password: String, fragment: Fragment): LoginResult<FirebaseUser> {
        return try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(fragment.requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signIn:success")
                        if (auth.currentUser?.displayName != null) {
                            Toast.makeText(
                                fragment.context,
                                fragment.context?.getString(
                                    R.string.login_success,
                                    auth.currentUser!!.displayName!!
                                ),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signIn:failure", task.exception)
                        Toast.makeText(
                            fragment.context, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .await()

            if (auth.currentUser != null) {
                LoginResult.Success(auth.currentUser)
            } else {
                LoginResult.Error(IOException("Error logging in"))
            }
        } catch (e: Throwable) {
            LoginResult.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        auth.signOut()
    }

    companion object {
        private const val TAG = "LoginDataSource"
    }
}