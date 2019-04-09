package com.mnvsngv.assignment4.backend

interface IBackend {
    fun loginOrRegister(id: String, password: String)
    fun isUserLoggedIn(): Boolean
}