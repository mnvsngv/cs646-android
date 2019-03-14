package com.mnvsngv.assignment3

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog

class GameOverDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder.setMessage(R.string.game_over)
                .setPositiveButton(R.string.ok, null)

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}