package com.mnvsngv.assignment4.activity

import android.animation.ObjectAnimator
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.mnvsngv.assignment4.R
import com.mnvsngv.assignment4.backend.IBackendListener
import com.mnvsngv.assignment4.data.Post
import com.mnvsngv.assignment4.singleton.BackendInstance
import kotlinx.android.synthetic.main.activity_new_post.*


class NewPostActivity : AppCompatActivity(), IBackendListener {

    private val backend = BackendInstance.getInstance(this, this)
    private lateinit var photoUri: Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        photoUri = intent.getParcelableExtra(URI_KEY)
        postImage.setImageURI(photoUri)

        submitPostButton.setOnClickListener {
            val currentUser = backend.getCurrentUser()
            if (currentUser != null) {
                uploadProgressBar.visibility = View.VISIBLE
                submitPostButton.visibility = View.INVISIBLE
                val id = currentUser.userID

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
        if (photoUri.authority == "com.mnvsngv.assignment4.fileprovider") {
            // Clean up after upload
            contentResolver.delete(photoUri, null, null)
        }

        setResult(Activity.RESULT_OK)
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
            if (length != 0) hashtags.add(caption.substring(index, index + length).toLowerCase())

            index = caption.indexOf('#', index)
        }

        return hashtags
    }

    companion object {
        const val URI_KEY = "uri-string"
    }
}
