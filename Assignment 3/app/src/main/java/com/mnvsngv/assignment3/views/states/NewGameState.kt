package com.mnvsngv.assignment3.views.states

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import com.mnvsngv.assignment3.MainActivity

class NewGameState : GameState {
    override fun init(view: View, width: Int, height: Int, listener: MainActivity.GameBarListener?) {

    }

    override fun drawOn(canvas: Canvas?) {

    }

    override fun handleTouch(event: MotionEvent): Boolean {
        return true
    }

    override fun state(): GameState.Constants {
        return GameState.Constants.NEW
    }
}