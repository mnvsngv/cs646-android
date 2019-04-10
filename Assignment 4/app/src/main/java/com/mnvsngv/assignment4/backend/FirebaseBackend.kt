package com.mnvsngv.assignment4.backend

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.mnvsngv.assignment4.R


private const val TAG = "FirebaseBackend"
private const val USERS_COLLECTION = "Users"

class FirebaseBackend(private val baseActivity: Activity, private val listener: IBackendListener): IBackend {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(baseActivity) {
                if (it.isSuccessful) {
                    listener.onLoginSuccess()
                } else {
                    handleLoginException(it.exception)
                }
            }
    }

    override fun register(email: String, userID: String, name: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(baseActivity) {
                if (it.isSuccessful) {
                    registerAndCreateUser(email, userID, name)
                    listener.onRegisterSuccess()
                } else {
                    handleRegisterException(it.exception)
                }
            }
    }

    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser == null
    }

    private fun registerAndCreateUser(email: String, userID: String, name: String) {
        // Create a new user with a first and last name
        val user = HashMap<String, Any>()
        user["email"] = email
        user["userID"] = userID
        user["name"] = name

        // Add a new document with a generated ID
        db.collection(USERS_COLLECTION)
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

        listener.onRegisterSuccess()
    }

    private fun handleLoginException(exception: Exception?) {
        when (exception) {
            // User doesn't exist
            is FirebaseAuthInvalidUserException -> {
                if (exception.errorCode == "ERROR_USER_NOT_FOUND") {
                    listener.onLoginFailure(baseActivity.getString(R.string.invalid_user))
                }
            }

            // Incorrect password
            is FirebaseAuthInvalidCredentialsException -> {
                listener.onLoginFailure(baseActivity.getString(R.string.invalid_credentials))
            }

            // Some other issue?
            else -> listener.onLoginFailure(baseActivity.getString(R.string.login_failure))
        }
    }

    private fun handleRegisterException(exception: java.lang.Exception?) {
        when (exception) {
            // Password too weak
            is FirebaseAuthWeakPasswordException -> {
                listener.onLoginFailure(baseActivity.getString(R.string.weak_password))
            }
        }
    }

}
