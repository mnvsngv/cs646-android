package com.mnvsngv.assignment3.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.MotionEvent
import android.widget.Toast
import com.mnvsngv.assignment3.MainActivity
import com.mnvsngv.assignment3.R
import com.mnvsngv.assignment3.views.states.DrawState
import com.mnvsngv.assignment3.views.states.GameState
import com.mnvsngv.assignment3.views.states.NewGameState
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
        return true
    }

    fun onMainButtonPressed() {
        when (currentState?.state()) {
            GameState.Constants.DRAW -> {
                val circles = (currentState as DrawState).circles
                if (circles.size == 0) {
                    Toast.makeText(context, context.getString(R.string.obstacle_warning), Toast.LENGTH_SHORT).show()
                } else {
                    currentState = PlayState(circles)
                    listener?.setMainButtonText(context.getString(R.string.end_game))
                }
            }

            GameState.Constants.PLAY -> {
                currentState = NewGameState()
                listener?.reset()
                listener?.setMainButtonText(context.getString(R.string.new_game))
            }

            GameState.Constants.NEW -> {
                currentState = DrawState()
                listener?.setMainButtonText(context.getString(R.string.start_game))
            }
        }
        currentState?.init(this, width, height, listener)
        invalidate()
    }

    fun onPauseButtonPressed() {
        if (currentState is PlayState) {
            val playState: PlayState = currentState as PlayState
            if (playState.isPaused) {
                playState.isPaused = false
                listener?.setPauseButtonText(context.getString(R.string.pause))
                invalidate()
            } else {
                playState.isPaused = true
                listener?.setPauseButtonText(context.getString(R.string.resume))
            }

        }
    }

}
