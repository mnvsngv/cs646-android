package com.mnvsngv.assignment3.views.states

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import com.mnvsngv.assignment3.GameBarListener

interface GameState {
    enum class Constants { DRAW, PLAY, NEW }
    fun init(view: View, width: Int, height: Int, listener: GameBarListener)
    fun drawOn(canvas: Canvas)
    fun handleTouch(event: MotionEvent): Boolean
    fun getState(): Constants
}