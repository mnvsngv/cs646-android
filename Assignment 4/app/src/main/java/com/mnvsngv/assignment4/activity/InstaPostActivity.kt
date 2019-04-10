package com.mnvsngv.assignment4.activity

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.mnvsngv.assignment4.R
import com.mnvsngv.assignment4.backend.FirebaseBackend
import com.mnvsngv.assignment4.backend.IBackendListener
import com.mnvsngv.assignment4.fragment.MainFragment
import com.mnvsngv.assignment4.fragment.PostRecyclerViewAdapter
import com.mnvsngv.assignment4.fragment.UserRecyclerViewAdapter
import com.mnvsngv.assignment4.fragment.dummy.DummyContent
import kotlinx.android.synthetic.main.activity_instapost.*
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

class InstaPostActivity : AppCompatActivity(), IBackendListener, MainFragment.OnListFragmentInteractionListener {
    override fun onListFragmentInteraction(item: DummyContent.DummyItem?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val backend = FirebaseBackend(this, this)

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

        actionBar?.title = getString(R.string.app_name)
        supportActionBar?.title = getString(R.string.app_name)
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        addPostFab.setOnClickListener { Toast.makeText(this, "fabulous.", Toast.LENGTH_SHORT).show() }
    }

    // Add the logout button to the action bar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.logout_button -> {
            backend.logout()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun onLogout() {
        startActivity(intentFor<LoginActivity>().newTask().clearTop())
        finish()
    }
}
