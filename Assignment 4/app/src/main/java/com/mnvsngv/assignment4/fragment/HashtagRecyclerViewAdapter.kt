package com.mnvsngv.assignment4.fragment

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mnvsngv.assignment4.R
import kotlinx.android.synthetic.main.fragment_hashtag.view.*


class HashtagRecyclerViewAdapter(
    private val hashtags: List<String>,
    private val listener: HashtagAdapterOnClickListener
) : RecyclerView.Adapter<HashtagRecyclerViewAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val hashtag = v.tag
            if (hashtag is String) {
                listener.handleHashtagClicked(hashtag)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_hashtag, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hashtag = hashtags[position]
        holder.hashtagView.text = hashtag

        with(holder.view) {
            tag = hashtag
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount(): Int = hashtags.size


    interface HashtagAdapterOnClickListener {
        fun handleHashtagClicked(hashtag: String)
    }


    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val hashtagView: TextView = view.hashtag

        override fun toString(): String {
            return super.toString() + " '" + hashtagView.text + "'"
        }
    }
}
