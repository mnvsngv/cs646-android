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
import android.view.View
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
private const val PICK_IMAGE = 2
private const val URI_KEY = "uriString"
private const val TAG = "instapost"

class InstaPostActivity : AppCompatActivity(), IBackendListener, MainFragment.OnFragmentInteractionListener {

    private val selectedItemKey = "selected-item"
    private val fragmentCache = mutableMapOf<ListType, MainFragment>()
    private var backend = BackendInstance.getInstance(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.mnvsngv.assignment4.R.layout.activity_instapost)

        actionBar?.title = getString(R.string.app_name)
        supportActionBar?.title = getString(R.string.app_name)
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        takePhotoFab.setOnClickListener {
            AddPost.captureImageForNewPost(this)
        }

        addFromGalleryFab.setOnClickListener {
            AddPost.fromGallery(this)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(selectedItemKey, navigation.selectedItemId)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState?.getInt(selectedItemKey)?.let { navigation.selectedItemId = it }
    }

    override fun onResume() {
        super.onResume()
        backend = BackendInstance.getInstance(this, this)

        // TODO prevent this from refreshing every time!
//        onNavigationItemSelectedListener.onNavigationItemSelected(navigation.menu.findItem(R.id.navigation_home))
//        navigation.selectedItemId = R.id.navigation_home
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

    override fun onFinishedLoading(hasPosts: Boolean) {
        progressBar.visibility = View.INVISIBLE
        if (hasPosts) {
            noPostsText.visibility = View.INVISIBLE
        } else {
            noPostsText.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> startActivity<NewPostActivity>(URI_KEY to AddPost.photoURI)
                PICK_IMAGE -> startActivity<NewPostActivity>(URI_KEY to data?.data)
            }
        }
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        noPostsText.visibility = View.INVISIBLE
        when (item.itemId) {
            R.id.navigation_home -> {
                takePhotoFab.show()
                addFromGalleryFab.show()
                replaceFragmentWith(ListType.POSTS)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                takePhotoFab.hide()
                addFromGalleryFab.hide()
                replaceFragmentWith(ListType.USERS)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                takePhotoFab.hide()
                addFromGalleryFab.hide()
                replaceFragmentWith(ListType.HASHTAGS)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun replaceFragmentWith(listType: ListType) {
        val fragment = fragmentCache.getOrElse(listType) {
            Log.i(TAG, "$listType not cached!")
            progressBar.visibility = View.VISIBLE
            val fragment = MainFragment.newInstance(listType)
            fragmentCache[listType] = fragment
            fragment
        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }

    private object AddPost {
        lateinit var photoURI: Uri

        fun captureImageForNewPost(activity: Activity) {

            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(activity.packageManager)?.also {

                    val photoFile: File = AddPost.createImageFile(activity)

                    val photoURI: Uri = FileProvider.getUriForFile(
                        activity,
                        "com.mnvsngv.assignment4.fileprovider",
                        photoFile
                    )

                    this.photoURI = photoURI
                    addPictureToGallery(activity)

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }

        }

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

        private fun addPictureToGallery(activity: Activity) {
            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                val f = File(this.photoURI.path)
                mediaScanIntent.data = Uri.fromFile(f)
                activity.sendBroadcast(mediaScanIntent)
            }
        }

        fun fromGallery(activity: Activity) {


            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
        }
    }
}
