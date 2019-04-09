package com.mnvsngv.assignment4.backend

interface IBackendListener {
    fun onLoginSuccess()
    fun onLoginFailure(message: String)
}