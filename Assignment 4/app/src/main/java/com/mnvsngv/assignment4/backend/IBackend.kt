package com.mnvsngv.assignment4.backend

import android.net.Uri
import com.mnvsngv.assignment4.dataclass.Post
import com.mnvsngv.assignment4.dataclass.User

interface IBackend {
    fun login(email: String, password: String)
    fun register(email: String, userID: String, name: String, password: String)
    fun logout()
    fun isUserLoggedIn(): Boolean
    fun uploadNewPost(post: Post, photoUri: Uri, hashtags: List<String>)
    fun getPost(postID: String)
    fun getAllUsers()
    fun getAllPosts()
    fun getAllPostsFor(user: User)
    fun getAllHashtags()
    fun getAllPostsFor(hashtag: String)
}