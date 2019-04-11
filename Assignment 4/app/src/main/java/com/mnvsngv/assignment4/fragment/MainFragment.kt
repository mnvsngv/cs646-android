package com.mnvsngv.assignment4.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mnvsngv.assignment4.R
import com.mnvsngv.assignment4.activity.LoginActivity
import com.mnvsngv.assignment4.activity.UserPostsActivity
import com.mnvsngv.assignment4.backend.IBackend
import com.mnvsngv.assignment4.backend.IBackendListener
import com.mnvsngv.assignment4.dataclass.Post
import com.mnvsngv.assignment4.dataclass.User
import com.mnvsngv.assignment4.singleton.BackendInstance
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.newTask
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.startActivity


class MainFragment : Fragment(), IBackendListener, UserRecyclerViewAdapter.UserAdapterOnClickListener {

    private lateinit var listType: ListType
    private lateinit var backend: IBackend
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            listType = it.getSerializable(ARG_ADAPTER_TYPE) as ListType
            user = it.getSerializable(ARG_USER) as User?
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        backend = BackendInstance.getInstance(context as Activity, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_container, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                adapter = when(listType) {
                    ListType.POSTS -> {
                        if (user != null) {
                            backend.getAllPostsFor(user as User)
                        } else {
                            backend.getAllPosts()
                        }
                        PostRecyclerViewAdapter(emptyList())
                    }
                    ListType.USERS -> {
                        backend.getAllUsers()
                        UserRecyclerViewAdapter(emptyList(), this@MainFragment)
                    }
                    ListType.HASHTAGS -> PostRecyclerViewAdapter(emptyList())
                }
            }
        }
        return view
    }

    override fun onLogout() {
        startActivity(intentFor<LoginActivity>().newTask().clearTop())
        activity?.finish()
    }

    override fun onGetAllPosts(posts: List<Post>) {
        if (view is RecyclerView) {
            with(view as RecyclerView) {
                adapter = PostRecyclerViewAdapter(posts)
            }
        }
    }

    override fun onGetAllPostsForUser(posts: List<Post>) {
        if (view is RecyclerView) {
            with(view as RecyclerView) {
                adapter = PostRecyclerViewAdapter(posts)
            }
        }
    }

    override fun onGetAllUsers(users: List<User>) {
        if (view is RecyclerView) {
            with(view as RecyclerView) {
                adapter = UserRecyclerViewAdapter(users, this@MainFragment)
            }
        }

    }

    override fun handleUserClicked(user: User) {
        startActivity<UserPostsActivity>(ARG_USER to user)
    }

    companion object {

        const val ARG_ADAPTER_TYPE = "adapter-type"
        const val ARG_USER = "user"

        @JvmStatic
        fun newInstance(listType: ListType, user: User? = null) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_ADAPTER_TYPE, listType)
                    putSerializable(ARG_USER, user)
                }
            }
    }
}
