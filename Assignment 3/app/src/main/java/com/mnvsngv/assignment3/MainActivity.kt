package com.mnvsngv.assignment3

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var score = 0
    private var lives = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        score = 0
//        lives = 0

        mainButton.setOnClickListener { gameView.onMainButtonPressed() }
        gameView.setListener(object: GameBarListener {
            override fun increaseScore() {
                score++
                scoreView.text = score.toString()
            }

            override fun decreaseLife() {
                lives--
                livesView.text = lives.toString()
            }
        })
    }

    interface GameBarListener {
        fun increaseScore()
        fun decreaseLife()
    }

}


