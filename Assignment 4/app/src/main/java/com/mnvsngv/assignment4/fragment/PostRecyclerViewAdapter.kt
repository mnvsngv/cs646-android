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
import com.mnvsngv.assignment4.R
import com.mnvsngv.assignment4.backend.IBackendListener
import com.mnvsngv.assignment4.dataclass.Post
import kotlinx.android.synthetic.main.fragment_post.view.*
import java.lang.ref.WeakReference
import java.nio.ByteBuffer


private const val TAG = "PostAdapter"

class PostRecyclerViewAdapter(private val posts: List<Post>) :
        RecyclerView.Adapter<PostRecyclerViewAdapter.ViewHolder>(), IBackendListener {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Post
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        holder.userIDView.text = post.userID
        holder.captionView.text = post.caption
        DownloadImageTask(holder.photoView).execute(post.uriString)

        with(holder.view) {
            tag = post
            setOnClickListener(mOnClickListener)
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


    class DownloadImageTask(photoView: ImageView) : AsyncTask<String, Void, ByteArray>() {
        private var weakPhotoView: WeakReference<ImageView> = WeakReference(photoView)

        override fun doInBackground(vararg params: String?): ByteArray {
            val inputStream = java.net.URL(params[0]).openStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)

            val byteBuffer = ByteBuffer.allocate(bitmap.byteCount);
            bitmap.copyPixelsToBuffer(byteBuffer);
            return byteBuffer.array()
        }

        override fun onPostExecute(result: ByteArray?) {
//            weakPhotoView.get()?.imageBitmap = result

            val photoView = weakPhotoView.get()
            if (photoView != null && result != null) {
                photoView.setImageBitmap(
                    decodeSampledBitmapFromByteArray(result, photoView.width, photoView.height)
                )
            }
        }

        private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
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

        private fun decodeSampledBitmapFromByteArray(array: ByteArray, reqWidth: Int, reqHeight: Int): Bitmap {
            // First decode with inJustDecodeBounds=true to check dimensions
            return BitmapFactory.Options().run {
                inJustDecodeBounds = true
                BitmapFactory.decodeByteArray(array,0, array.size, this)

                // Calculate inSampleSize
                inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

                // Decode bitmap with inSampleSize set
                inJustDecodeBounds = false

                BitmapFactory.decodeByteArray(array,0, array.size, this)
            }
        }

    }
}
