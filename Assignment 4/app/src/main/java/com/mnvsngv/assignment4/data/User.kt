package com.mnvsngv.assignment4.data

import java.io.Serializable

data class User(val email: String, val userID: String, val name: String) : Serializable {
    // Empty constructor for Firebase to convert its result into a User object
    constructor() : this("", "", "")
}