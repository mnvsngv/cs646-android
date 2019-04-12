package com.mnvsngv.assignment4.activity

import android.animation.ObjectAnimator
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.mnvsngv.assignment4.R
import com.mnvsngv.assignment4.backend.IBackendListener
import com.mnvsngv.assignment4.dataclass.Post
import com.mnvsngv.assignment4.dataclass.User
import com.mnvsngv.assignment4.singleton.BackendInstance
import com.mnvsngv.assignment4.singleton.CurrentSession
import kotlinx.android.synthetic.main.activity_new_post.*


private const val URI_KEY = "uriString"

class NewPostActivity : AppCompatActivity(), IBackendListener {

    private val backend = BackendInstance.getInstance(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        val photoUri = intent.getParcelableExtra<Uri>(URI_KEY)
        postImage.setImageURI(photoUri)
        submitPostButton.setOnClickListener {
            if (CurrentSession.user != null) {
                uploadProgressBar.visibility = View.VISIBLE
                submitPostButton.visibility = View.INVISIBLE
                val id = (CurrentSession.user as User).userID

                val fileName = photoUri.lastPathSegment
                if (fileName != null) {
                    val post = Post(id, fileName, captionInput.text.toString(), System.currentTimeMillis())
                    backend.uploadNewPost(post, photoUri, findHashtagsIn(post.caption))
                }
            }
        }
    }

    override fun onUpdateUploadProgress(progress: Int) {
        ObjectAnimator.ofInt(uploadProgressBar, "progress", progress)
            .setDuration(750)
            .start()
    }

    override fun onUploadSuccess() {
        uploadProgressBar.visibility = View.INVISIBLE
        finish()
    }

    private fun findHashtagsIn(caption: String): List<String> {
        val hashtags = arrayListOf<String>()

        var index = caption.indexOf('#')
        while (index >= 0) {

            var length = 0
            index++
            while (index+length < caption.length && caption[index+length].isLetterOrDigit()) {
                length++
            }
            if (length != 0) hashtags.add(caption.substring(index, index + length))

            index = caption.indexOf('#', index)
        }

        return hashtags
    }
}
