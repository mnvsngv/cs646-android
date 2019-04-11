package com.mnvsngv.assignment4.dataclass

import java.io.Serializable

data class User(val email: String, val userID: String, val name: String) : Serializable {
    constructor() : this("", "", "")
}