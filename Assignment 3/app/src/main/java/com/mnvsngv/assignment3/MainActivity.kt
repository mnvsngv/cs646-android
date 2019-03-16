package com.mnvsngv.assignment3

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), GameBarListener {

    // Track the game state
    private var score = 0
    private var lives = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // What happens when the buttons are pressed, depends on what state the game is in.
        // MainActivity doesn't know about that, but gameView does. So it's invoked for button presses.
        mainButton.setOnClickListener { gameView.onMainButtonPressed() }
        pauseButton.setOnClickListener { gameView.onPauseButtonPressed() }

        gameView.setListener(this)
    }

    override fun increaseScore() {
        score++
        scoreView.text = score.toString()
    }

    override fun decreaseLife() {
        lives--
        livesView.text = lives.toString()
        if (lives == 0) {
            gameView.endGame()
            GameOverDialog().show(supportFragmentManager, "game_over")
        }
    }

    override fun setMainButtonText(text: String) {
        mainButton.text = text
    }

    override fun setPauseButtonText(text: String) {
        pauseButton.text = text
    }

    override fun resetGame() {
        scoreView.text = getString(R.string.default_score)
        livesView.text = getString(R.string.default_lives)
        pauseButton.text = getString(R.string.pause)
        score = 0
        lives = 3
    }

}


