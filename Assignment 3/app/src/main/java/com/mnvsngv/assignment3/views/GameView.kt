package com.mnvsngv.assignment3.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.MotionEvent
import com.mnvsngv.assignment3.MainActivity
import com.mnvsngv.assignment3.views.states.DrawState
import com.mnvsngv.assignment3.views.states.GameState
import com.mnvsngv.assignment3.views.states.PlayState


class GameView : View, View.OnTouchListener {

    private var currentState: GameState? = null
    private var listener: MainActivity.GameBarListener? = null

    constructor(context: Context): super(context)
    constructor(context: Context, attributes: AttributeSet): super(context,attributes) {
        setOnTouchListener(this)
        currentState = DrawState()
    }

    fun setListener(listener: MainActivity.GameBarListener) {
        this.listener = listener
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        currentState?.init(this, width, height, listener)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        currentState?.drawOn(canvas)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (currentState != null)
            return (currentState as GameState).handleTouch(event)
        return false
    }

    fun onMainButtonPressed() {
        when (currentState?.state()) {
            GameState.StateConstants.DRAW -> {
                val circles = (currentState as DrawState).circles
                currentState = PlayState(circles)
            }
            GameState.StateConstants.PLAY -> currentState = DrawState()
        }
        currentState?.init(this, width, height, listener)
        invalidate()
    }

}
