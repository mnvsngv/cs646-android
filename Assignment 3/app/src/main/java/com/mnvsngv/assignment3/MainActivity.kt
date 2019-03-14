package com.mnvsngv.assignment3

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var score = 0
    private var lives = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val context = this

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
                if (lives == 0) {
                    gameView.onPauseButtonPressed()
                    gameView.endGame()
                    Toast.makeText(context, getString(R.string.game_over), Toast.LENGTH_SHORT).show()
                }
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


