package com.mnvsngv.assignment4.activity

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mnvsngv.assignment4.R
import com.mnvsngv.assignment4.backend.FirebaseBackend
import com.mnvsngv.assignment4.backend.IBackendListener
import com.mnvsngv.assignment4.dataclass.Post
import com.mnvsngv.assignment4.dataclass.User
import com.mnvsngv.assignment4.singleton.CurrentSession
import kotlinx.android.synthetic.main.activity_new_post.*


private const val URI_KEY = "uri"

class NewPostActivity : AppCompatActivity(), IBackendListener {

    private val backend = FirebaseBackend(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        val photoUri = intent.getParcelableExtra<Uri>(URI_KEY)
        postImage.setImageURI(photoUri)
        submitPostButton.setOnClickListener {
            if (CurrentSession.user != null) {
                val id = (CurrentSession.user as User).userID
                val post = Post(id, photoUri, captionInput.text.toString())
                backend.uploadNewPost(post)
            }
            finish()
        }
    }
}
