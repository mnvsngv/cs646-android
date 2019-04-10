package com.mnvsngv.assignment4.backend

interface IBackendListener {
    fun onLoginSuccess() {}
    fun onLoginFailure(messageID: Int) {}
    fun onRegisterSuccess() {}
    fun onRegisterFailure(messageID: Int) {}
}