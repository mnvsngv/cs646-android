package com.mnvsngv.assignment4.fragment

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mnvsngv.assignment4.R
import com.mnvsngv.assignment4.data.User


import kotlinx.android.synthetic.main.fragment_user.view.*


class UserRecyclerViewAdapter(
    private val users: List<User>,
    private val listener: UserAdapterOnClickListener
) : RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val user = v.tag
            if (user is User) {
                listener.handleUserClicked(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.userIDView.text = user.userID

        with(holder.view) {
            tag = user
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount(): Int = users.size


    interface UserAdapterOnClickListener {
        fun handleUserClicked(user: User)

    }


    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val userIDView: TextView = view.user_id

        override fun toString(): String {
            return super.toString() + " '" + userIDView.text + "'"
        }
    }
}
