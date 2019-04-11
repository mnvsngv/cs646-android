package com.mnvsngv.assignment4.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mnvsngv.assignment4.R
import com.mnvsngv.assignment4.fragment.dummy.DummyContent
import com.mnvsngv.assignment4.fragment.dummy.DummyContent.DummyItem


class MainFragment : Fragment() {

    private lateinit var listType: ListType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            listType = it.getSerializable(ARG_ADAPTER_TYPE) as ListType
        }
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
                    ListType.POSTS -> PostRecyclerViewAdapter(DummyContent.ITEMS)
                    ListType.USERS -> UserRecyclerViewAdapter(DummyContent.ITEMS)
                    ListType.HASHTAGS -> UserRecyclerViewAdapter(DummyContent.ITEMS)
                }
            }
        }
        return view
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
