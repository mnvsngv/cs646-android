package com.mnvsngv.assignment2.data_classes

import java.io.Serializable

data class Question(var question: String, var options: MutableList<String>, var answer: Int): Serializable {
    constructor() : this("", ArrayList(), -1)
}