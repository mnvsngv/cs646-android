package com.mnvsngv.assignment4.fragment


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

        Glide.with(holder.view).load(post.uriString).into(holder.photoView)

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
}
