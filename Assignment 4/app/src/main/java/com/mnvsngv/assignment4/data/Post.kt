package com.mnvsngv.assignment4.data

data class Post(val userID: String, val photoFileName: String,
                val caption: String, val timestamp: Long,
                var uriString: String? = null) {
    // Empty constructor for Firebase to convert its result into a Post object
    constructor() : this("", "", "", 0)
}