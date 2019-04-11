package com.mnvsngv.assignment4.dataclass

data class Post(val userID: String, val photoFileName: String, val caption: String, var uriString: String? = null) {
    constructor() : this("", "", "")
}