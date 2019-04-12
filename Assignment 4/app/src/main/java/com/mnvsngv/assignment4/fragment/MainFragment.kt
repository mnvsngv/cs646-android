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
import com.mnvsngv.assignment4.dataclass.Post
import com.mnvsngv.assignment4.dataclass.User
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
    private var user: User? = null
    private var hashtag: String? = null
    private val hashtagPosts = arrayListOf<Post>()
    private var numberOfHashtagPosts = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            listType = it.getSerializable(ARG_ADAPTER_TYPE) as ListType
            user = it.getSerializable(ARG_USER) as User?
            hashtag = it.getString(ARG_HASHTAG)
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
                        } else if(hashtag != null) {
                            backend.getAllPostsFor(hashtag as String)
                        } else {
                            backend.getAllPosts()
                        }
                        PostRecyclerViewAdapter(emptyList())
                    }
                    ListType.USERS -> {
                        backend.getAllUsers()
                        UserRecyclerViewAdapter(emptyList(), this@MainFragment)
                    }
                    ListType.HASHTAGS -> {
                        backend.getAllHashtags()
                        HashtagRecyclerViewAdapter(emptyList(), this@MainFragment)
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

    override fun onGetAllHashtags(hashtags: List<String>) {
        if (view is RecyclerView) {
            with(view as RecyclerView) {
                adapter = HashtagRecyclerViewAdapter(hashtags, this@MainFragment)
            }
        }
    }

    override fun onGetAllPostsForHashtag(postIDs: List<String>) {
//        if (view is RecyclerView) {
//            with(view as RecyclerView) {
//                Log.i(TAG, "Creating adapter")
//                adapter = PostRecyclerViewAdapter(hashtagPosts)
//            }
//        }
        numberOfHashtagPosts = postIDs.size
        Log.i(TAG, "Need to get $numberOfHashtagPosts posts")
        for (postID in postIDs) {
            Log.i(TAG, "Getting $postID...")
            backend.getPost(postID)
        }
    }

    override fun onGetPost(post: Post) {
        Log.i(TAG, "Adding ${post.photoFileName}")
        hashtagPosts.add(post)
        if (hashtagPosts.size == numberOfHashtagPosts) {
            Log.i(TAG, "Added all posts!")
            if (view is RecyclerView) {
                with(view as RecyclerView) {
//                    adapter?.notifyDataSetChanged()
                    adapter = PostRecyclerViewAdapter(hashtagPosts)
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

    companion object {

        const val ARG_ADAPTER_TYPE = "adapter-type"
        const val ARG_USER = "user"
        const val ARG_HASHTAG = "hashtag"

        @JvmStatic
        fun newInstance(listType: ListType, user: User? = null, hashtag: String? = null) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_ADAPTER_TYPE, listType)
                    putSerializable(ARG_USER, user)
                    putString(ARG_HASHTAG, hashtag)
                }
            }
    }
}
