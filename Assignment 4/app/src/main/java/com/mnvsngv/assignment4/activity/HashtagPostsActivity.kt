package com.mnvsngv.assignment4.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mnvsngv.assignment4.R
import com.mnvsngv.assignment4.fragment.ListType
import com.mnvsngv.assignment4.fragment.MainFragment

class HashtagPostsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_posts)

        val hashtag = intent.getSerializableExtra(MainFragment.ARG_HASHTAG) as String

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, MainFragment.newInstance(ListType.POSTS, hashtag=hashtag))
        transaction.commit()
    }
}