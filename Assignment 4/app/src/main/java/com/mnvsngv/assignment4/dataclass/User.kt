package com.mnvsngv.assignment4.dataclass

data class User(val email: String, val userID: String, val name: String) {
    constructor() : this("", "", "")
}