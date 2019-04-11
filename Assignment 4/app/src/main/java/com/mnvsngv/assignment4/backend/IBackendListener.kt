package com.mnvsngv.assignment4.backend

import com.mnvsngv.assignment4.dataclass.Post

interface IBackendListener {
    fun onLoginSuccess() {}
    fun onLoginFailure(messageID: Int) {}
    fun onRegisterSuccess() {}
    fun onRegisterFailure(messageID: Int) {}
    fun onLogout() {}
    fun onUploadSuccess() {}
    fun onUpdateUploadProgress(progress: Int) {}
    fun onGetAllPosts(posts: List<Post>) {}
}