package com.mnvsngv.assignment3

// Similar to how fragments interact with parent activities without knowing about the concrete activity,
// this listener allows the game view and its state to talk with the parent activity.
interface GameBarListener {
    fun increaseScore()
    fun decreaseLife()
    fun setMainButtonText(text: String)
    fun setPauseButtonText(text: String)
    fun resetGame()
}