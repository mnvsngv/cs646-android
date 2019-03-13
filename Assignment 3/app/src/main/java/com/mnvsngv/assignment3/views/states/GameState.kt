package com.mnvsngv.assignment3.views.states

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View

interface GameState {
    enum class StateConstants { DRAW, PLAY }
    fun init(view: View, width: Int, height: Int)
    fun drawOn(canvas: Canvas?)
    fun handleTouch(event: MotionEvent): Boolean
    fun state(): StateConstants
}