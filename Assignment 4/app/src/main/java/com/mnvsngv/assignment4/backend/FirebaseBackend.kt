package com.mnvsngv.assignment4.backend

import android.app.Activity
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.mnvsngv.assignment4.R
import com.mnvsngv.assignment4.dataclass.Post
import com.mnvsngv.assignment4.dataclass.User
import com.mnvsngv.assignment4.singleton.CurrentSession


private const val TAG = "FirebaseBackend"
private const val USERS_COLLECTION = "Users"
private const val POSTS_COLLECTION = "Posts"
private const val HASHTAGS_COLLECTION = "Hashtags"

class FirebaseBackend(private val baseActivity: Activity, var listener: IBackendListener): IBackend {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(baseActivity) {
                if (it.isSuccessful) {
                    getUserData(email)
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
                    Log.w(TAG, it.exception)
                    handleRegisterException(it.exception)
                }
            }
    }

    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser == null
    }

    override fun logout() {
        auth.signOut()
        listener.onLogout()
    }

    override fun uploadNewPost(post: Post, photoUri: Uri, hashtags: List<String>) {
        val storageRef = storage.reference
        val photoRef = storageRef.child("${post.userID}/${post.photoFileName}")

        // Upload file
        photoRef.putFile(photoUri)
            .addOnProgressListener {
                val progress = it.bytesTransferred * 100 / it.totalByteCount
                listener.onUpdateUploadProgress(progress.toInt())
            }
            .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
                return@Continuation photoRef.downloadUrl
            })
            .addOnCompleteListener {
                // Once the file is uploaded, add the post metadata
                post.uriString = it.result.toString()

                for (hashtag in hashtags) {
                    db.collection(HASHTAGS_COLLECTION).document(hashtag)
                        .set(mapOf(post.photoFileName to ""), SetOptions.merge())
                }

                db.collection(POSTS_COLLECTION).document(post.photoFileName)
                    .set(post)
                    .addOnSuccessListener {
                        // And once this is done, we can inform the parent activity
                        listener.onUploadSuccess()
                    }
        }
    }

    override fun getAllPosts() {
        db.collection(POSTS_COLLECTION)
            .orderBy("photoFileName", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val posts = arrayListOf<Post>()
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val post = document.toObject(Post::class.java)
                    posts.add(post)
                }
                listener.onGetAllPosts(posts)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    override fun getAllPostsFor(user: User) {
        db.collection(POSTS_COLLECTION)
            .whereEqualTo("userID", user.userID)
            .orderBy("photoFileName", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val posts = arrayListOf<Post>()
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val post = document.toObject(Post::class.java)
                    posts.add(post)
                }
                listener.onGetAllPostsForUser(posts)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    override fun getAllUsers() {
        db.collection(USERS_COLLECTION)
            .get()
            .addOnSuccessListener { result ->
                val users = arrayListOf<User>()
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val user = document.toObject(User::class.java)
                    users.add(user)
                }
                listener.onGetAllUsers(users)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    private fun registerAndCreateUser(email: String, userID: String, name: String) {
        // Create a new user with a first and last name
        val user = HashMap<String, Any>()
        user["email"] = email
        user["userID"] = userID
        user["name"] = name

        // Add a new document with a generated ID
        db.collection(USERS_COLLECTION).document(email)
            .set(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: $email")
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
                listener.onLoginFailure(R.string.invalid_user)
            }

            // Incorrect password
            is FirebaseAuthInvalidCredentialsException -> {
                listener.onLoginFailure(R.string.invalid_credentials)
            }

            // Some other issue?
            else -> listener.onLoginFailure(R.string.login_failure)
        }
    }

    private fun handleRegisterException(exception: java.lang.Exception?) {
        when (exception) {
            // Password too weak
            is FirebaseAuthWeakPasswordException -> {
                listener.onRegisterFailure(R.string.weak_password)
            }

            // User already exists
            is FirebaseAuthUserCollisionException -> {
                listener.onRegisterFailure(R.string.user_already_exists)
            }
        }
    }

    private fun getUserData(userID: String) {
        val docRef = db.collection(USERS_COLLECTION).document(userID)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    CurrentSession.user = document.toObject(User::class.java)
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

}
