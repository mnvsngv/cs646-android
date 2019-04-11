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
import com.mnvsngv.assignment4.backend.IBackend
import com.mnvsngv.assignment4.backend.IBackendListener
import com.mnvsngv.assignment4.dataclass.Post
import com.mnvsngv.assignment4.fragment.dummy.DummyContent
import com.mnvsngv.assignment4.fragment.dummy.DummyContent.DummyItem
import com.mnvsngv.assignment4.singleton.BackendInstance


class MainFragment : Fragment(), IBackendListener {

    private lateinit var listType: ListType
    private lateinit var backend: IBackend

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            listType = it.getSerializable(ARG_ADAPTER_TYPE) as ListType
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
                        backend.getAllPosts()
                        PostRecyclerViewAdapter(emptyList())
                    }
                    ListType.USERS -> UserRecyclerViewAdapter(DummyContent.ITEMS)
                    ListType.HASHTAGS -> UserRecyclerViewAdapter(DummyContent.ITEMS)
                }
            }
        }
        return view
    }

    override fun onGetAllPosts(posts: List<Post>) {
        if (view is RecyclerView) {
            with(view as RecyclerView) {
                adapter = PostRecyclerViewAdapter(posts)
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: DummyItem?)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"
        const val ARG_ADAPTER_TYPE = "adapter-type"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(listType: ListType) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_ADAPTER_TYPE, listType)
                }
            }
    }
}
