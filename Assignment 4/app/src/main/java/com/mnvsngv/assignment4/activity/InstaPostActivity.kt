package com.mnvsngv.assignment4.activity

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.mnvsngv.assignment4.R
import com.mnvsngv.assignment4.fragment.MainFragment
import com.mnvsngv.assignment4.fragment.PostRecyclerViewAdapter
import com.mnvsngv.assignment4.fragment.UserRecyclerViewAdapter
import com.mnvsngv.assignment4.fragment.dummy.DummyContent
import kotlinx.android.synthetic.main.activity_instapost.*

class InstaPostActivity : AppCompatActivity(), MainFragment.OnListFragmentInteractionListener {
    override fun onListFragmentInteraction(item: DummyContent.DummyItem?) {

    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                addPostFab.show()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragmentContainer, MainFragment.newInstance(1, PostRecyclerViewAdapter(DummyContent.ITEMS, this)))
                transaction.commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                addPostFab.hide()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragmentContainer, MainFragment.newInstance(1, UserRecyclerViewAdapter(DummyContent.ITEMS, this)))
                transaction.commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                addPostFab.hide()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instapost)

        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        addPostFab.setOnClickListener { Toast.makeText(this, "fabulous.", Toast.LENGTH_SHORT).show() }
    }
}
