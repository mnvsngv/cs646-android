package com.mnvsngv.assignment4.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mnvsngv.assignment4.R
import kotlinx.android.synthetic.main.activity_new_post.*

private const val URI_KEY = "uri"

class NewPostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        postImage.setImageURI(intent.getParcelableExtra(URI_KEY))
        submitPostButton.setOnClickListener { finish() }
    }
}
