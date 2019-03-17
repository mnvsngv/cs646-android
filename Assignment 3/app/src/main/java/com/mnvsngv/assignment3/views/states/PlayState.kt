package com.mnvsngv.assignment3.views.states

import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import com.mnvsngv.assignment3.GameBarListener
import com.mnvsngv.assignment3.R
import com.mnvsngv.assignment3.views.dataclasses.Obstacle
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.math.pow

class PlayState(private val obstacles: ArrayList<Obstacle>) : GameState {

    private lateinit var view: View
    private var playerX = 0f
    private var playerY = 0f
    private val playerRadius = 25f
    private val playerPaint = Paint()
    private val obstaclePaint = Paint()
    private var width = 0f
    private var height = 0f
    private val scheduler = Executors.newScheduledThreadPool(1)
    private var task: ScheduledFuture<*>? = null
    private var isInMotion = false
    private enum class Direction { LEFT, RIGHT }
    private lateinit var motionDirection: Direction
    private var resetDistance = 0f
    private var numInvisible = 0
    private val maxVelocity = 35f
    private lateinit var listener: GameBarListener
    var isPaused = false
    var hasGameEnded = false

    override fun init(view: View, width: Int, height: Int, listener: GameBarListener) {
        this.view = view
        this.width = width.toFloat()
        this.height = height.toFloat()
        playerX = width / 2f
        playerY = height * 0.9f
        obstaclePaint.style = Paint.Style.STROKE
        obstaclePaint.strokeWidth = 3f

        // Save the height of the lowest circle, as we'll use it as reference to reset all circles
        // in the same pattern once they all fall off the screen.
        for (obstacle in obstacles) {
            if (obstacle.centreY > resetDistance) {
                resetDistance = obstacle.centreY
            }
        }

        this.listener = listener
    }


    override fun drawOn(canvas: Canvas) {

        // Draw the player circle
        canvas.drawCircle(playerX, playerY, playerRadius, playerPaint)

        for (obstacle in obstacles) {
            if (obstacle.visible && !obstacle.hasHitPlayer) {
                canvas.drawCircle(obstacle.centreX, obstacle.centreY, obstacle.radius, obstaclePaint)
            }

            if (!isPaused) {  // If we're not paused then move the obstacle & perform collision detection
                if (!obstacle.hasHitPlayer && isPlayerHit(obstacle)) {  // Collision detection
                    listener.decreaseLife()
                    obstacle.hasHitPlayer = true
                }

                if (obstacle.visible && (obstacle.centreY - obstacle.radius) > height) {  // Obstacle now off screen?
                    numInvisible++
                    obstacle.visible = false
                    if (!obstacle.hasHitPlayer) {
                        listener.increaseScore()
                    }
                    obstacle.hasHitPlayer = false
                }

                // Regardless of whether the obstacle is visible or if it has hit the player,
                // move the obstacle so we can reset it accurately.
                obstacle.centreY += obstacle.velocity

            } else {  // We're paused so show the "paused" text
                val textPaint = Paint()
                textPaint.textSize = 50f
                textPaint.textAlign = Paint.Align.CENTER
                if (!hasGameEnded) {  // Only show the text if we haven't finished the game
                    canvas.drawText(view.context.getString(R.string.paused), width/2, height/2, textPaint)
                }
            }
        }

        // If all the circles have moved off screen, then reset them to above the top of the screen & speed them up.
        if (numInvisible == obstacles.size) {
            for (obstacle in obstacles) {
                obstacle.visible = true
                obstacle.centreY -= (height + resetDistance)
                if (obstacle.velocity <= maxVelocity)  // Don't let the obstacles fall too fast!
                    obstacle.velocity *= 1.25f
            }
            numInvisible = 0
        }

        // Only invalidate & redraw if we're not paused.
        // Otherwise the user will not be able to see the circles on the screen while paused.
        if (!isPaused) {
            view.invalidate()
        }
    }

    override fun handleTouch(event: MotionEvent): Boolean {
        when (event.action and MotionEvent.ACTION_MASK) {  // Need the mask as we don't need additional fingers' IDs
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {  // Move the player circle on touch
                startMoving(getDirection(event))
                return true
            }

            MotionEvent.ACTION_UP -> {  // Stop moving the circle when touch stops
                stopMotion()
                return true
            }
        }
        return false
    }

    override fun getState(): GameState.Constants {
        return GameState.Constants.PLAY
    }

    private fun startMoving(directionToMove: Direction) {
        if (isInMotion && directionToMove != motionDirection) {
            // If user tries to move in the opposite direction of current motion, stop the motion.
            task?.cancel(true)
            isInMotion = false
        } else if(!isInMotion && !isPaused) {
            // If the circle isn't moving right now then start moving it
            task = scheduler.scheduleAtFixedRate({ move(directionToMove) },
                0, 100, TimeUnit.MICROSECONDS
            )
            isInMotion = true
        }
    }

    private fun stopMotion() {
        isInMotion = false
        task?.cancel(true)
    }

    private fun move(directionToMove: Direction) {
        motionDirection = directionToMove
        if (motionDirection == Direction.RIGHT) {
            if (playerX + playerRadius < width) {  // Bounds check
                playerX += 0.1f
            }
        }
        else {
            if (playerX - playerRadius > 0) {  // Bounds check
                playerX -= 0.1f
            }
        }
    }

    private fun getDirection(event: MotionEvent): Direction {
        // event.x returns the first finger's x co-ordinate
        // but we need the newest finger's x co-ordinate, so the following code.
        return if (event.getX(event.pointerCount - 1) > width / 2) {
            Direction.RIGHT
        } else {
            Direction.LEFT
        }
    }

    private fun isPlayerHit(obstacle: Obstacle): Boolean {
        // Collision detection: Check if Euclidean distance of centres of 2 circles is more than sum of the radii
        val centreDistance = (playerX - obstacle.centreX).pow(2) + (playerY - obstacle.centreY).pow(2)
        val minimumDistance = (playerRadius + obstacle.radius).pow(2)
        return centreDistance < minimumDistance
    }

}
