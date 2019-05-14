package com.arghyam.landing.ui.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.arghyam.R
import com.arghyam.commons.utils.Constants.TAG_HOME
import com.arghyam.commons.utils.Constants.TAG_MORE
import com.arghyam.commons.utils.Constants.TAG_MY_ACTIVITY
import com.arghyam.commons.utils.Constants.TAG_SEARCH
import com.arghyam.landing.ui.fragment.HomeFragment
import com.arghyam.more.ui.MoreFragment
import com.arghyam.myactivity.ui.MyActivityFragment
import com.arghyam.search.ui.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class LandingActivity : AppCompatActivity() {

    var CURRENT_TAG : String = TAG_HOME
    lateinit var navView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)
        navView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        val fragment = HomeFragment.newInstance()
        addFragment(fragment)
    }

    private val onNavigationItemSelectedListener = object : BottomNavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.navigation_home -> {
                    CURRENT_TAG = TAG_HOME
                    val fragment = HomeFragment.newInstance()
                    addFragment(fragment)
                    return true
                }
                R.id.navigation_search -> {
                    CURRENT_TAG = TAG_SEARCH
                    val fragment = SearchFragment.newInstance()
                    addFragment(fragment)
                    return true
                }
                R.id.navigation_my_activty -> {
                    CURRENT_TAG = TAG_MY_ACTIVITY
                    val fragment = MyActivityFragment.newInstance()
                    addFragment(fragment)
                    return true
                }
                R.id.navigation_more -> {
                    CURRENT_TAG = TAG_MORE
                    val fragment = MoreFragment.newInstance()
                    addFragment(fragment)
                    return true
                }
            }
            return false
        }

    }

    /**
     * add/replace fragment in container [framelayout]
     */
    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content, fragment, fragment.javaClass.simpleName)
            .addToBackStack(fragment.javaClass.getSimpleName())
            .commit()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        if(CURRENT_TAG == TAG_HOME) {
            finish()
        } else {
            CURRENT_TAG = TAG_HOME
            navView.menu[0].isChecked = true
            val fragment = HomeFragment.newInstance()
            addFragment(fragment)
        }
    }

}
