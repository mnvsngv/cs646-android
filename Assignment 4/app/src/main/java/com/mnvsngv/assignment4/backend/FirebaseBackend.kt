package com.mnvsngv.assignment4.backend

import android.app.Activity
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.mnvsngv.assignment4.R
import java.lang.Exception


private const val TAG = "FirebaseBackend"

class FirebaseBackend(private val baseActivity: Activity, private val listener: IBackendListener): IBackend {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun loginOrRegister(id: String, password: String) {
        checkInUser(id, password, auth::signInWithEmailAndPassword)
    }

    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser == null
    }

    private fun checkInUser(id: String, password: String, firebaseFunction: (String, String) -> Task<AuthResult>) {
        firebaseFunction(id, password)
            .addOnCompleteListener(baseActivity) {
                if (it.isSuccessful) {
                    listener.onLoginSuccess()
                } else {
                    handleFirebaseException(id, password, it.exception)
                }
            }
    }

    private fun handleFirebaseException(id: String, password: String, exception: Exception?) {
        when (exception) {
            is FirebaseAuthInvalidUserException -> {
                if (exception.errorCode == "ERROR_USER_NOT_FOUND") {
                    checkInUser(id, password, auth::createUserWithEmailAndPassword)
                }
            }

            is FirebaseAuthWeakPasswordException -> {
                listener.onLoginFailure(baseActivity.getString(R.string.weak_password))
            }

            is FirebaseAuthInvalidCredentialsException -> {
                listener.onLoginFailure(baseActivity.getString(R.string.invalid_credentials))
            }

            else -> listener.onLoginFailure(baseActivity.getString(R.string.login_failure))
        }
    }

    /*// Access a Cloud Firestore instance from your Activity
        val db = FirebaseFirestore.getInstance()

        // Create a new user with a first and last name
        val user = HashMap<String, Any>()
        user["first"] = "Ada"
        user["last"] = "Lovelace"
        user["born"] = 1815

        // Add a new document with a generated ID
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }*/

}