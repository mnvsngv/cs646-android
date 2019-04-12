package com.mnvsngv.assignment4.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.mnvsngv.assignment4.R
import com.mnvsngv.assignment4.fragment.ListType
import com.mnvsngv.assignment4.fragment.MainFragment
import kotlinx.android.synthetic.main.activity_user_posts.*

class HashtagPostsActivity : AppCompatActivity(), MainFragment.OnFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_posts)

        val hashtag = intent.getSerializableExtra(MainFragment.ARG_HASHTAG) as String
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = "#$hashtag"
        supportActionBar?.title = "#$hashtag"

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, MainFragment.newInstance(ListType.POSTS, hashtag=hashtag))
        transaction.commit()
    }

    override fun onFinishedLoading() {
        progressBar.visibility = View.INVISIBLE
    }
}