package com.mnvsngv.assignment1

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = "Assignment1 Activity"
    private lateinit var preferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        // Initializing the view
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferences = getPreferences(Context.MODE_PRIVATE)
        reset_button.setOnClickListener {

            val initialCount = getString(R.string.initial_count)

            onCreateCount.text = initialCount
            onRestartCount.text = initialCount
            onStartCount.text = initialCount
            onPauseCount.text = initialCount
            onStopCount.text = initialCount
            onDestroyCount.text = initialCount
            onResumeCount.text = initialCount
            onSaveInstanceCount.text = initialCount
            onRestoreInstanceCount.text = initialCount

            with(preferences.edit()) {
                clear()
                commit()
            }
        }

        // Now that we've initialized the counts, we can register that the onCreate
        registerMethodCalled("onCreate", onCreateCount)

        // If this was called because of an orientation change, then the bundle should not be null and so we can
        // display its values
        if (savedInstanceState != null) {
            Log.i(TAG, "Getting values from bundle...")
            displayFromBundle(savedInstanceState)

        }

        onCreateCount.text = preferences.getInt("onCreate", 0).toString()
        onRestartCount.text = preferences.getInt("onRestart", 0).toString()
        onStartCount.text = preferences.getInt("onStart", 0).toString()
        onPauseCount.text = preferences.getInt("onPause", 0).toString()
        onStopCount.text = preferences.getInt("onStop", 0).toString()
        onDestroyCount.text = preferences.getInt("onDestroy", 0).toString()
        onResumeCount.text = preferences.getInt("onResume", 0).toString()
        onSaveInstanceCount.text = preferences.getInt("onSaveInstanceState", 0).toString()
        onRestoreInstanceCount.text = preferences.getInt("onRestoreInstanceState", 0).toString()
    }

    override fun onRestart() {
        registerMethodCalled("onRestart", onRestartCount)
        super.onRestart()
    }

    override fun onStart() {
        registerMethodCalled("onStart", onStartCount)
        super.onStart()
    }

    override fun onPause() {
        registerMethodCalled("onPause", onPauseCount)
        super.onPause()
    }

    override fun onStop() {
        registerMethodCalled("onStop", onStopCount)
        super.onStop()
    }

    override fun onDestroy() {
        registerMethodCalled("onDestroy", onDestroyCount)
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        registerMethodCalled("onResume", onResumeCount)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        registerMethodCalled("onSaveInstanceState", onSaveInstanceCount)

        outState?.putCharSequence("onCreate", onCreateCount.text)
        outState?.putCharSequence("onStart", onStartCount.text)
        outState?.putCharSequence("onPause", onPauseCount.text)
        outState?.putCharSequence("onRestart", onRestartCount.text)
        outState?.putCharSequence("onStop", onStopCount.text)
        outState?.putCharSequence("onDestroy", onDestroyCount.text)
        outState?.putCharSequence("onResume", onResumeCount.text)
        outState?.putCharSequence("onSaveInstanceState", onSaveInstanceCount.text)
        outState?.putCharSequence("onRestoreInstanceState", onRestoreInstanceCount.text)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        registerMethodCalled("onRestoreInstanceState", onRestoreInstanceCount)
        displayFromBundle(savedInstanceState)
    }

    private fun registerMethodCalled(methodName: String, viewToUpdate: TextView) {
        val methodCount = preferences.getInt(methodName, 0)
        val newCount = methodCount + 1

        with(preferences.edit()) {
            putInt(methodName, newCount)
            commit()
        }

        Log.i(TAG, "Method $methodName called $newCount times")
        viewToUpdate.text = newCount.toString()
    }

    private fun displayFromBundle(bundle: Bundle?) {
        Log.i(TAG, "onCreate in Bundle: ${bundle?.getString("onCreate")}")
        Log.i(TAG, "onStart in Bundle: ${bundle?.getString("onStart")}")
        Log.i(TAG, "onPause in Bundle: ${bundle?.getString("onPause")}")
        Log.i(TAG, "onRestart in Bundle: ${bundle?.getString("onRestart")}")
        Log.i(TAG, "onStop in Bundle: ${bundle?.getString("onStop")}")
        Log.i(TAG, "onDestroy in Bundle: ${bundle?.getString("onDestroy")}")
        Log.i(TAG, "onResume in Bundle: ${bundle?.getString("onResume")}")
        Log.i(TAG, "onSaveInstanceState in Bundle: ${bundle?.getString("onSaveInstanceState")}")
        Log.i(TAG, "onRestoreInstanceState in Bundle: ${bundle?.getString("onRestoreInstanceState")}")
    }

}
