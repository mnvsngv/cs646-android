package com.mnvsngv.assignment3.views.states

import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import com.mnvsngv.assignment3.GameBarListener
import com.mnvsngv.assignment3.views.dataclasses.Obstacle
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class DrawState : GameState {

    private var view: View? = null
    private val scheduler = Executors.newScheduledThreadPool(1)
    private var task: ScheduledFuture<*>? = null
    val obstacles = ArrayList<Obstacle>()
    private val paint = Paint()

    override fun init(view: View, width: Int, height: Int, listener: GameBarListener?) {
        this.view = view
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
    }

    // Draw all the obstacles.
    override fun drawOn(canvas: Canvas?) {
        for (obstacle in obstacles) {
            canvas?.drawCircle(obstacle.centreX, obstacle.centreY, obstacle.radius, paint)
        }
    }

    override fun handleTouch(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                drawObstacle(event)
                return true
            }
            MotionEvent.ACTION_UP -> {
                stopDrawingObstacle()
                return true
            }
        }
        return false
    }

    override fun getState(): GameState.Constants {
        return GameState.Constants.DRAW
    }

    private fun drawObstacle(event: MotionEvent) {
        val circle = Obstacle(event.x, event.y, 0f, 5f, true, false)
        obstacles.add(circle)

        // Keep expanding the circle till we want to stop
        task = scheduler.scheduleAtFixedRate({
            circle.radius += 0.5f
            view?.invalidate()
        }, 0, 1, TimeUnit.MILLISECONDS)
    }

    private fun stopDrawingObstacle() {
        task?.cancel(true)
    }
}