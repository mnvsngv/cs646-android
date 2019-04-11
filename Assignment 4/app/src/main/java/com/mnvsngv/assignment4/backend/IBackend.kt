package com.mnvsngv.assignment4.backend

import com.mnvsngv.assignment4.dataclass.Post

interface IBackend {
    fun login(email: String, password: String)
    fun register(email: String, userID: String, name: String, password: String)
    fun logout()
    fun isUserLoggedIn(): Boolean
    fun uploadNewPost(post: Post)
}