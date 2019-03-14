package com.mnvsngv.assignment3.views.states

import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import com.mnvsngv.assignment3.MainActivity
import com.mnvsngv.assignment3.views.dataclasses.Obstacle
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.math.pow

class PlayState(private val obstacles: ArrayList<Obstacle>) : GameState {

    private var view: View? = null
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
    private var motionDirection: Direction? = null
    private var resetDistance = 0f
    private var numInvisible = 0
    private var listener: MainActivity.GameBarListener? = null
    var isPaused = false

    override fun init(view: View, width: Int, height: Int, listener: MainActivity.GameBarListener?) {
        this.view = view
        this.width = width.toFloat()
        this.height = height.toFloat()
        playerX = width / 2f
        playerY = height * 0.9f
        obstaclePaint.style = Paint.Style.STROKE
        obstaclePaint.strokeWidth = 3f

        for (obstacle in obstacles) {
            if (obstacle.centreY > resetDistance) {
                resetDistance = obstacle.centreY
            }
        }

        this.listener =  listener
    }

    override fun drawOn(canvas: Canvas?) {
        canvas?.drawCircle(playerX, playerY, playerRadius, playerPaint)

        for (obstacle in obstacles) {
            if (obstacle.visible && !obstacle.hasHitPlayer) {
                canvas?.drawCircle(obstacle.centreX, obstacle.centreY, obstacle.radius, obstaclePaint)
            }
            if (!isPaused) {

                obstacle.centreY += obstacle.velocity

                if (!obstacle.hasHitPlayer && isPlayerHit(obstacle)) {
                    listener?.decreaseLife()
                    obstacle.hasHitPlayer = true
                }

                if (obstacle.visible && (obstacle.centreY - obstacle.radius) > height) {
                    numInvisible++
                    obstacle.visible = false
                    if (!obstacle.hasHitPlayer) {
                        listener?.increaseScore()
                    }
                    obstacle.hasHitPlayer = false
                }

            }
        }
        if (numInvisible == obstacles.size) {
            for (obstacle in obstacles) {
                obstacle.visible = true
                obstacle.centreY -= (height + resetDistance)
                obstacle.velocity *= 1.25f
            }
            numInvisible = 0
        }
        if (!isPaused) {
            view?.invalidate()
        }
    }

    override fun handleTouch(event: MotionEvent): Boolean {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                startMoving(getDirection(event))
                return true
            }
            MotionEvent.ACTION_UP -> {
                stopMotion()
                return true
            }
            MotionEvent.ACTION_POINTER_UP -> {
                if (event.pointerCount == 1) {
                    startMoving(getDirection(event))
                }
                return true
            }
        }
        return false
    }

    override fun state(): GameState.Constants {
        return GameState.Constants.PLAY
    }

    private fun startMoving(directionToMove: Direction?) {
        if (isInMotion && directionToMove != motionDirection) {
            task?.cancel(true)
            isInMotion = false
        } else if(!isInMotion) {
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

    private fun move(directionToMove: Direction?) {
        motionDirection = directionToMove
        if (motionDirection == Direction.RIGHT) {
            if (playerX + playerRadius < width) {
                playerX += 0.1f
            }
        }
        else {
            if (playerX - playerRadius > 0) {
                playerX -= 0.1f
            }
        }
    }

    private fun getDirection(event: MotionEvent): Direction? {
        // event.x returns the first finger's x co-ordinate
        // but we need the newest finger's x co-ordinate, so the following code.
        return if (event.getX(event.pointerCount - 1) > width / 2) {
            Direction.RIGHT
        } else {
            Direction.LEFT
        }
    }

    private fun isPlayerHit(obstacle: Obstacle): Boolean {
        val centreDistance = (playerX - obstacle.centreX).pow(2) + (playerY - obstacle.centreY).pow(2)
        val minimumDistance = (playerRadius + obstacle.radius).pow(2)
        // W'll add a small buffer in the player's favour to compensate for calculation inaccuracies
        return centreDistance < minimumDistance + 3f
    }

}
