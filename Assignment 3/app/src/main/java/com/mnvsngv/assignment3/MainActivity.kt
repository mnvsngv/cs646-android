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
    private var lives = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainButton.setOnClickListener { gameView.onMainButtonPressed() }
        pauseButton.setOnClickListener { gameView.onPauseButtonPressed() }

        gameView.setListener(object: GameBarListener {
            override fun increaseScore() {
                score++
                scoreView.text = score.toString()
            }

            override fun decreaseLife() {
                lives--
                livesView.text = lives.toString()
            }

            override fun setMainButtonText(text: String) {
                mainButton.text = text
            }

            override fun setPauseButtonText(text: String) {
                pauseButton.text = text
            }

            override fun reset() {
                scoreView.text = getString(R.string.default_score)
                livesView.text = getString(R.string.default_lives)
                pauseButton.text = getString(R.string.pause)
                score = 0
                lives = 3
            }
        })
    }

    interface GameBarListener {
        fun increaseScore()
        fun decreaseLife()
        fun setMainButtonText(text: String)
        fun setPauseButtonText(text: String)
        fun reset()
    }

}


