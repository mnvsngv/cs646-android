package com.mnvsngv.assignment4.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mnvsngv.assignment4.R
import com.mnvsngv.assignment4.activity.HashtagPostsActivity
import com.mnvsngv.assignment4.activity.LoginActivity
import com.mnvsngv.assignment4.activity.UserPostsActivity
import com.mnvsngv.assignment4.backend.IBackend
import com.mnvsngv.assignment4.backend.IBackendListener
import com.mnvsngv.assignment4.data.ListType
import com.mnvsngv.assignment4.data.Post
import com.mnvsngv.assignment4.data.User
import com.mnvsngv.assignment4.singleton.BackendInstance
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.newTask
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.startActivity


private const val TAG = "MainFragment"

class MainFragment : Fragment(), IBackendListener,
    UserRecyclerViewAdapter.UserAdapterOnClickListener,
    HashtagRecyclerViewAdapter.HashtagAdapterOnClickListener {

    private lateinit var listType: ListType
    private lateinit var backend: IBackend
    private lateinit var listener: OnFragmentInteractionListener
    private var invalidateCache = false
    private var user: User? = null
    private var hashtag: String? = null
    private var fragmentPosts = mutableListOf<Post>()
    private var fragmentUsers = mutableListOf<User>()
    private var fragmentHashtags = mutableListOf<String>()
    private var numberOfHashtagPosts = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            listType = it.getSerializable(ARG_ADAPTER_TYPE) as ListType
            invalidateCache = it.getBoolean(ARG_INVALIDATE_CACHE)
            user = it.getSerializable(ARG_USER) as User?
            hashtag = it.getString(ARG_HASHTAG)
        }

        backend = BackendInstance.getInstance(context as Activity, this)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as OnFragmentInteractionListener
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
                        if (fragmentPosts.isEmpty() || invalidateCache) {
                            when {
                                user != null -> backend.getAllPostsFor(user as User)
                                hashtag != null -> backend.getAllPostsFor(hashtag as String)
                                else -> backend.getAllPosts()
                            }
                        }
                        PostRecyclerViewAdapter(fragmentPosts)
                    }

                    ListType.USERS -> {
                        if (fragmentUsers.isEmpty() || invalidateCache) backend.getAllUsers()
                        UserRecyclerViewAdapter(fragmentUsers, this@MainFragment)
                    }

                    ListType.HASHTAGS -> {
                        if (fragmentHashtags.isEmpty() || invalidateCache) backend.getAllHashtags()
                        HashtagRecyclerViewAdapter(fragmentHashtags, this@MainFragment)
                    }
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
        fragmentPosts.removeAll { true }
        fragmentPosts.addAll(posts)
        if (view is RecyclerView) {
            with(view as RecyclerView) {
                adapter?.notifyDataSetChanged()
            }
        }
        listener.onFinishedLoading(posts.isNotEmpty())
    }

    override fun onGetAllPostsForUser(posts: List<Post>) {
        fragmentPosts.removeAll { true }
        fragmentPosts.addAll(posts)
        if (view is RecyclerView) {
            with(view as RecyclerView) {
                adapter?.notifyDataSetChanged()
            }
        }
        listener.onFinishedLoading(posts.isNotEmpty())
    }

    override fun onGetAllUsers(users: List<User>) {
        fragmentUsers.removeAll { true }
        fragmentUsers.addAll(users)
        if (view is RecyclerView) {
            with(view as RecyclerView) {
                adapter?.notifyDataSetChanged()
            }
        }
        listener.onFinishedLoading(users.isNotEmpty())
    }

    override fun onGetAllHashtags(hashtags: List<String>) {
        fragmentHashtags.removeAll { true }
        fragmentHashtags.addAll(hashtags)
        if (view is RecyclerView) {
            with(view as RecyclerView) {
                adapter?.notifyDataSetChanged()
            }
        }
        listener.onFinishedLoading(hashtags.isNotEmpty())
    }

    override fun onGetAllPostsForHashtag(postIDs: List<String>) {
        fragmentPosts.removeAll { true }
        numberOfHashtagPosts = postIDs.size
        Log.i(TAG, "Need to get $numberOfHashtagPosts fragmentPosts")
        for (postID in postIDs) {
            Log.i(TAG, "Getting $postID...")
            backend.getPost(postID)
        }
    }

    override fun onGetPost(post: Post) {
        fragmentPosts.add(post)
        if (fragmentPosts.size == numberOfHashtagPosts) {
            fragmentPosts.sortBy { it.timestamp }

            if (view is RecyclerView) {
                with(view as RecyclerView) {
                    adapter?.notifyDataSetChanged()
                    listener.onFinishedLoading(fragmentPosts.isNotEmpty())
                }
            }
        }
    }

    override fun handleUserClicked(user: User) {
        startActivity<UserPostsActivity>(ARG_USER to user)
    }

    override fun handleHashtagClicked(hashtag: String) {
        startActivity<HashtagPostsActivity>(ARG_HASHTAG to hashtag)
    }

    fun refreshPosts() {
        backend = BackendInstance.getInstance(context as Activity, this)
        backend.getAllPosts()
    }

    interface OnFragmentInteractionListener {
        fun onFinishedLoading(hasPosts: Boolean)
    }

    companion object {

        const val ARG_ADAPTER_TYPE = "adapter-type"
        const val ARG_USER = "user"
        const val ARG_HASHTAG = "hashtag"
        const val ARG_INVALIDATE_CACHE = "invalidate-cache"

        @JvmStatic
        fun newInstance(listType: ListType, invalidateCache: Boolean = false,
                        user: User? = null, hashtag: String? = null) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_ADAPTER_TYPE, listType)
                    putBoolean(ARG_INVALIDATE_CACHE, invalidateCache)
                    putSerializable(ARG_USER, user)
                    putString(ARG_HASHTAG, hashtag)
                }
            }
    }
}
