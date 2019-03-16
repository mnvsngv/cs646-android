package com.mnvsngv.assignment3.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.mnvsngv.assignment3.GameBarListener
import com.mnvsngv.assignment3.R
import com.mnvsngv.assignment3.views.states.DrawState
import com.mnvsngv.assignment3.views.states.GameState
import com.mnvsngv.assignment3.views.states.NewGameState
import com.mnvsngv.assignment3.views.states.PlayState


class GameView : View, View.OnTouchListener {

    // State pattern, since the game can have multiple states.
    private var currentState: GameState? = null

    // Listener to allow interaction with parent activity that holds the view
    private var listener: GameBarListener? = null

    constructor(context: Context): super(context)
    constructor(context: Context, attributes: AttributeSet): super(context,attributes) {
        setOnTouchListener(this)
        currentState = DrawState()  // Start off in the draw state so players can start putting obstacles
    }

    fun setListener(listener: GameBarListener) {
        this.listener = listener
    }

    // Once the view's been constructed and we have a height & width, we can create the initial state.
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        currentState?.init(this, width, height, listener)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        currentState?.drawOn(canvas)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (currentState != null)  // Otherwise we return Boolean? instead of Boolean
            return (currentState as GameState).handleTouch(event)
        return true
    }

    // Handle changing the state
    fun onMainButtonPressed() {
        when (currentState?.getState()) {
            GameState.Constants.DRAW -> {  // Moves to the play state
                val circles = (currentState as DrawState).obstacles
                if (circles.size == 0) {
                    Toast.makeText(context, context.getString(R.string.obstacle_warning), Toast.LENGTH_SHORT).show()
                } else {
                    currentState = PlayState(circles)
                    listener?.setMainButtonText(context.getString(R.string.end_game))
                }
            }

            GameState.Constants.PLAY -> {  // Moves to the end state
                currentState = NewGameState()
                listener?.setMainButtonText(context.getString(R.string.new_game))
            }

            GameState.Constants.NEW -> {  // Moves to the draw state
                currentState = DrawState()
                listener?.setMainButtonText(context.getString(R.string.start_game))
                listener?.resetGame()
            }
        }
        invalidate()  // Update the canvas to reflect the new state
        currentState?.init(this, width, height, listener)
    }

    fun endGame() {
        // Shouldn't be ppossible to be in any other state at this point but check for safety
        if (currentState is PlayState) {
            val playState: PlayState = currentState as PlayState
            playState.isPaused = true
            playState.hasGameEnded = true
            currentState = NewGameState()
            listener?.setMainButtonText(context.getString(R.string.new_game))
        }
    }

    fun onPauseButtonPressed() {
        // Shouldn't be ppossible to be in any other state at this point but check for safety
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
