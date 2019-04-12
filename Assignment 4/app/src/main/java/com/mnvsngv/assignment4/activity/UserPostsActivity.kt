package com.mnvsngv.assignment4.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mnvsngv.assignment4.R
import com.mnvsngv.assignment4.dataclass.User
import com.mnvsngv.assignment4.fragment.ListType
import com.mnvsngv.assignment4.fragment.MainFragment
import com.mnvsngv.assignment4.fragment.MainFragment.Companion.ARG_USER

class UserPostsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_posts)

        val user = intent.getSerializableExtra(ARG_USER) as User
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = "${user.userID}'s posts"
        supportActionBar?.title = "${user.userID}'s posts"

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, MainFragment.newInstance(ListType.POSTS, user=user))
        transaction.commit()
    }
}
