package com.mnvsngv.assignment4.backend

import android.app.Activity
import android.net.Uri
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.mnvsngv.assignment4.R
import com.mnvsngv.assignment4.data.Post
import com.mnvsngv.assignment4.data.User


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
                    handleRegisterException(it.exception)
                }
            }
    }

    override fun getCurrentUser(): User? {
        return currentUser
    }

    override fun logout() {
        auth.signOut()
        currentUser = null
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
                // Convert Task<Uri> result into the actual Uri result
                return@Continuation photoRef.downloadUrl
            })
            .addOnCompleteListener {
                // Once the file is uploaded, add the post metadata
                post.uriString = it.result.toString()

                for (hashtag in hashtags) {
                    // Using SetOptions.merge() so new posts are added to existing hashtags
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

    override fun getPost(postID: String) {
        db.collection(POSTS_COLLECTION).document(postID)
            .get()
            .addOnSuccessListener { result ->
                val post = result.toObject(Post::class.java)
                if (post != null) {
                    listener.onGetPost(post)
                }
            }
    }

    override fun getAllPosts() {
        db.collection(POSTS_COLLECTION)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val posts = arrayListOf<Post>()
                for (document in result) {
                    val post = document.toObject(Post::class.java)
                    posts.add(post)
                }
                listener.onGetAllPosts(posts)
            }
    }

    override fun getAllPostsFor(user: User) {
        db.collection(POSTS_COLLECTION)
            .whereEqualTo("userID", user.userID)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val posts = arrayListOf<Post>()
                for (document in result) {
                    val post = document.toObject(Post::class.java)
                    posts.add(post)
                }
                listener.onGetAllPostsForUser(posts)
            }
    }

    override fun getAllHashtags() {
        db.collection(HASHTAGS_COLLECTION)
            .get()
            .addOnSuccessListener { result ->
                val hashtags = arrayListOf<String>()
                for (hashtag in result) {
                    hashtags.add(hashtag.id)
                }
                listener.onGetAllHashtags(hashtags)
            }
    }

    override fun getAllPostsFor(hashtag: String) {
        db.collection(HASHTAGS_COLLECTION).document(hashtag)
            .get()
            .addOnSuccessListener { result ->
                val hashtags = result.data?.keys?.toList()
                if (hashtags != null) {
                    listener.onGetAllPostsForHashtag(hashtags)
                }
            }
    }

    override fun getAllUsers() {
        db.collection(USERS_COLLECTION)
            .get()
            .addOnSuccessListener { result ->
                val users = arrayListOf<User>()
                for (document in result) {
                    val user = document.toObject(User::class.java)
                    users.add(user)
                }
                listener.onGetAllUsers(users)
            }
    }


    private fun registerAndCreateUser(email: String, userID: String, name: String) {
        val user = HashMap<String, String>()
        user["email"] = email
        user["userID"] = userID
        user["name"] = name

        // Add the user to Firebase
        db.collection(USERS_COLLECTION).document(email)
            .set(user)
            .addOnSuccessListener {
                listener.onRegisterSuccess()
            }

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
        db.collection(USERS_COLLECTION).document(userID)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    currentUser = document.toObject(User::class.java)
                }
            }
    }


    private companion object {
        private var currentUser: User? = null
    }

}
