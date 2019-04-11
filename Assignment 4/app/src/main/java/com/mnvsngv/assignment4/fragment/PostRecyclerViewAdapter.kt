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
import org.jetbrains.anko.imageBitmap
import java.lang.ref.WeakReference


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


    class DownloadImageTask(photoView: ImageView) : AsyncTask<String, Void, Bitmap>() {
        private var weakPhotoView: WeakReference<ImageView> = WeakReference(photoView)

        override fun doInBackground(vararg params: String?): Bitmap {
            val inputStream = java.net.URL(params[0]).openStream()
            return BitmapFactory.decodeStream(inputStream)
        }

        override fun onPostExecute(result: Bitmap?) {
            weakPhotoView.get()?.imageBitmap = result
        }

    }
}
