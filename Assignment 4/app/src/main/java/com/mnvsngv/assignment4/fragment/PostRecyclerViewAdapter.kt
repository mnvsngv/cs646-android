package com.mnvsngv.assignment4.fragment


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mnvsngv.assignment4.R
import com.mnvsngv.assignment4.data.Post
import kotlinx.android.synthetic.main.fragment_post.view.*
import java.io.InputStream
import java.lang.ref.WeakReference


class PostRecyclerViewAdapter(private val posts: List<Post>) :
        RecyclerView.Adapter<PostRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        holder.userIDView.text = post.userID
        holder.captionView.text = post.caption

        // TODO Replace with AsyncTask
        Glide.with(holder.view).load(post.uriString).into(holder.photoView)
//        DownloadImageTask(holder.photoView).execute(post.uriString)

        with(holder.view) {
            tag = post
        }
    }

    override fun getItemCount(): Int = posts.size


    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val userIDView: TextView = view.user_id
        val captionView: TextView = view.caption
        val photoView: ImageView = view.post_photo

        override fun toString(): String {
            return super.toString() + " '" + captionView.text + "'"
        }
    }

    private class DownloadImageTask(imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {

        private var imageViewReference = WeakReference(imageView)

        override fun doInBackground(vararg params: String?): Bitmap? {
            val downloadUrl = java.net.URL(params[0])
            val imageView = imageViewReference.get()
            if (imageView != null) {
                return decodeSampledBitmapFromStream(
                    downloadUrl.openStream(),
                    imageView.width,
                    imageView.height
                )
            }
            return null
        }

        override fun onPostExecute(result: Bitmap?) {
            if (result != null) {
                imageViewReference.get()?.setImageBitmap(result)
            }
        }

        fun decodeSampledBitmapFromStream(inputStream: InputStream, reqWidth: Int, reqHeight: Int): Bitmap? {
            // First decode with inJustDecodeBounds=true to check dimensions
            return BitmapFactory.Options().run {
                inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

                // Decode bitmap with inSampleSize set
                inJustDecodeBounds = false

                BitmapFactory.decodeStream(inputStream, null, this)
            }
        }

        fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            // Raw height and width of image
            val (height: Int, width: Int) = options.run { outHeight to outWidth }
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {

                val halfHeight: Int = height / 2
                val halfWidth: Int = width / 2

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                    inSampleSize *= 2
                }
            }

            return inSampleSize
        }

    }
}
