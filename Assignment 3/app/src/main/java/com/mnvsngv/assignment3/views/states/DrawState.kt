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

class DrawState : GameState {

    private var view: View? = null
    private val scheduler = Executors.newScheduledThreadPool(1)
    private var task: ScheduledFuture<*>? = null
    val circles = ArrayList<Obstacle>()
    private val paint = Paint()

    override fun init(view: View, width: Int, height: Int, listener: MainActivity.GameBarListener?) {
        this.view = view
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
    }

    override fun drawOn(canvas: Canvas?) {
        for (circle in circles) {
            canvas?.drawCircle(circle.centreX, circle.centreY, circle.radius, paint)
        }
    }

    override fun handleTouch(event: MotionEvent): Boolean {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                drawCircle(event)
                return true
            }
            MotionEvent.ACTION_UP -> {
                stopDrawingCircle()
                return true
            }
        }
        return false
    }

    override fun state(): GameState.Constants {
        return GameState.Constants.DRAW
    }

    private fun drawCircle(event: MotionEvent) {
        val newCircle = Obstacle(event.x, event.y, 0f, 5f, true, false)
        circles.add(newCircle)
        task = scheduler.scheduleAtFixedRate({
            newCircle.radius += 0.5f
            view?.invalidate()
        }, 0, 1, TimeUnit.MILLISECONDS)
    }

    private fun stopDrawingCircle() {
        task?.cancel(true)
    }
}