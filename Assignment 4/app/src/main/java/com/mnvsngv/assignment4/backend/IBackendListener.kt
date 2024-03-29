package com.mnvsngv.assignment4.backend

import com.mnvsngv.assignment4.data.Post
import com.mnvsngv.assignment4.data.User

interface IBackendListener {
    fun onLoginSuccess() {}
    fun onLoginFailure(messageID: Int) {}
    fun onRegisterSuccess() {}
    fun onRegisterFailure(messageID: Int) {}
    fun onLogout() {}
    fun onUploadSuccess() {}
    fun onUpdateUploadProgress(progress: Int) {}
    fun onGetPost(post: Post) {}
    fun onGetAllUsers(users: List<User>) {}
    fun onGetAllPosts(posts: List<Post>) {}
    fun onGetAllPostsForUser(posts: List<Post>) {}
    fun onGetAllHashtags(hashtags: List<String>) {}
    fun onGetAllPostsForHashtag(postIDs: List<String>) {}
}