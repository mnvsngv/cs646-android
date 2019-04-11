package com.mnvsngv.assignment4.backend

import com.mnvsngv.assignment4.dataclass.Post
import com.mnvsngv.assignment4.dataclass.User

interface IBackendListener {
    fun onLoginSuccess() {}
    fun onLoginFailure(messageID: Int) {}
    fun onRegisterSuccess() {}
    fun onRegisterFailure(messageID: Int) {}
    fun onLogout() {}
    fun onUploadSuccess() {}
    fun onUpdateUploadProgress(progress: Int) {}
    fun onGetAllPosts(posts: List<Post>) {}
    fun onGetAllPostsForUser(posts: List<Post>) {}
    fun onGetAllUsers(users: List<User>) {}
}