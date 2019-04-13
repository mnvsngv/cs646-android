package com.mnvsngv.assignment4.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.widget.SwipeRefreshLayout
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
import kotlinx.android.synthetic.main.fragment_list_container.view.*
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
    private lateinit var swipeContainer: SwipeRefreshLayout

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
//        backend = BackendInstance.getInstance(context as Activity, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_container, container, false)
        swipeContainer = view as SwipeRefreshLayout
        swipeContainer.setColorSchemeColors(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))

        // Set the adapter
        if (view.list is RecyclerView) {
            with(view.list) {
                adapter = when(listType) {

                    ListType.POSTS -> {
                        if (fragmentPosts.isEmpty()) {
                            when {
                                user != null -> {
                                    swipeContainer.setOnRefreshListener { refreshPostsFor(user as User) }
                                    backend.getAllPostsFor(user as User)
                                }
                                hashtag != null -> {
                                    swipeContainer.setOnRefreshListener { refreshPostsFor(hashtag as String) }
                                    backend.getAllPostsFor(hashtag as String)
                                }
                                else -> {
                                    swipeContainer.setOnRefreshListener { refreshPosts() }
                                    backend.getAllPosts()
                                }
                            }
                        } else {
                            swipeContainer.setOnRefreshListener { refreshPosts() }
                        }
                        PostRecyclerViewAdapter(fragmentPosts)
                    }

                    ListType.USERS -> {
                        if (fragmentUsers.isEmpty()) backend.getAllUsers()
                        swipeContainer.setOnRefreshListener { refreshUsers() }
                        UserRecyclerViewAdapter(fragmentUsers, this@MainFragment)
                    }

                    ListType.HASHTAGS -> {
                        if (fragmentHashtags.isEmpty()) backend.getAllHashtags()
                        swipeContainer.setOnRefreshListener { refreshHashtags() }
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
        refreshList(fragmentPosts, posts)
    }

    override fun onGetAllPostsForUser(posts: List<Post>) {
        refreshList(fragmentPosts, posts)
    }

    override fun onGetAllUsers(users: List<User>) {
        refreshList(fragmentUsers, users)
    }

    override fun onGetAllHashtags(hashtags: List<String>) {
        refreshList(fragmentHashtags, hashtags)
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

            if (view?.list is RecyclerView) {
                with(view?.list as RecyclerView) {
                    adapter?.notifyDataSetChanged()
                    swipeContainer.isRefreshing = false
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

    fun refreshPostsFor(user: User) {
        backend.getAllPostsFor(user)
    }

    fun refreshPostsFor(hashtag: String) {
        backend.getAllPostsFor(hashtag)
    }

    fun refreshPosts() {
        backend.getAllPosts()
    }

    fun refreshHashtags() {
        backend.getAllHashtags()
    }

    fun refreshUsers() {
        backend.getAllUsers()
    }

    private fun <T> refreshList(listToUpdate: MutableList<T>, newDataList: List<T>) {
        listToUpdate.removeAll { true }
        listToUpdate.addAll(newDataList)
        if (view?.list is RecyclerView) {
            with(view?.list as RecyclerView) {
                adapter?.notifyDataSetChanged()
            }
        }
        swipeContainer.isRefreshing = false
        listener.onFinishedLoading(newDataList.isNotEmpty())
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
