package com.mnvsngv.assignment4.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.BottomNavigationView
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.mnvsngv.assignment4.R
import com.mnvsngv.assignment4.backend.IBackendListener
import com.mnvsngv.assignment4.fragment.ListType
import com.mnvsngv.assignment4.fragment.MainFragment
import com.mnvsngv.assignment4.singleton.BackendInstance
import kotlinx.android.synthetic.main.activity_instapost.*
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


private const val REQUEST_IMAGE_CAPTURE = 1
private const val URI_KEY = "uriString"

class InstaPostActivity : AppCompatActivity(), IBackendListener {

    private var backend = BackendInstance.getInstance(this, this)

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                addPostFab.show()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragmentContainer, MainFragment.newInstance(ListType.POSTS))
                transaction.commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                addPostFab.hide()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragmentContainer, MainFragment.newInstance(ListType.USERS))
                transaction.commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                addPostFab.hide()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instapost)


        actionBar?.title = getString(R.string.app_name)
        supportActionBar?.title = getString(R.string.app_name)
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        addPostFab.setOnClickListener {
//            Toast.makeText(this, "fabulous.", Toast.LENGTH_SHORT).show()
            AddPost.captureImageForNewPost(this)
        }
    }

    override fun onResume() {
        super.onResume()
        backend = BackendInstance.getInstance(this, this)
    }

    // Add the logout button to the action bar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.logout_button -> {
            backend.logout()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun onLogout() {
        startActivity(intentFor<LoginActivity>().newTask().clearTop())
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.i("image_test", AddPost.photoURI.toString())
            startActivity<NewPostActivity>(URI_KEY to AddPost.photoURI)
//            val imageBitmap = data?.extras?.get("data") as Bitmap

//            imageView.setImageBitmap(imageBitmap)
        }
    }

    private object AddPost {
        lateinit var photoURI: Uri

        private fun createImageFile(context: Context): File {
            // Create an image file name
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
            )
        }

        fun captureImageForNewPost(activity: Activity) {
            val REQUEST_IMAGE_GET = 2

            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(activity.packageManager)?.also {

                    val photoFile: File = AddPost.createImageFile(activity)

                    val photoURI: Uri = FileProvider.getUriForFile(
                        activity,
                        "com.mnvsngv.assignment4.fileprovider",
                        photoFile
                    )

                    this.photoURI = photoURI

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }

//        startActivityForResult<AddPostActivity>(REQUEST_IMAGE_CAPTURE)
        }
    }
}
