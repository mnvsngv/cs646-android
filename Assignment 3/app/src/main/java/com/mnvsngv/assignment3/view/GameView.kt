package com.mnvsngv.assignment3.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.MotionEvent
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


class GameView : View, View.OnTouchListener {

    private var playerX = 0f
    private var playerY = 0f
    private val playerRadius = 25f
    private val playerPaint = Paint()
    private val scheduler = Executors.newScheduledThreadPool(1)
    private var task: ScheduledFuture<*>? = null
    private var isInMotion = false
    private enum class Direction { LEFT, RIGHT }
    private var motionDirection: Direction? = null


    constructor(context: Context): super(context)
    constructor(context: Context, attributes: AttributeSet): super(context,attributes) {
        setOnTouchListener(this)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        playerX = width / 2f
        playerY = height * 0.9f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawCircle(playerX, playerY, playerRadius, playerPaint)
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
        invalidate()
    }



    override fun onTouch(v: View, event: MotionEvent): Boolean {
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

    private fun getDirection(event: MotionEvent): Direction? {
        // event.x returns the first finger's x co-ordinate
        // but we need the newest finger's x co-ordinate, so the following code.
        return if (event.getX(event.pointerCount - 1) > width / 2) {
            Direction.RIGHT
        } else {
            Direction.LEFT
        }
    }



}
