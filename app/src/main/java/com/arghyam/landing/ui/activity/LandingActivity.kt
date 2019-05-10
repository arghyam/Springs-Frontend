package com.arghyam.landing.ui.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.arghyam.R
import com.arghyam.landing.ui.fragment.HomeFragment
import com.arghyam.more.ui.MoreFragment
import com.arghyam.myactivity.ui.MyActivityFragment
import com.arghyam.search.ui.SearchFragment

class LandingActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        val fragment = HomeFragment.Companion.newInstance()
        addFragment(fragment)
    }

    private val onNavigationItemSelectedListener = object : BottomNavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.navigation_home -> {
                    val fragment = HomeFragment.Companion.newInstance()
                    addFragment(fragment)
                    return true
                }
                R.id.navigation_search -> {
                    val fragment = SearchFragment.Companion.newInstance()
                    addFragment(fragment)
                    return true
                }
                R.id.navigation_my_activty -> {
                    val fragment = MyActivityFragment.Companion.newInstance()
                    addFragment(fragment)
                    return true
                }
                R.id.navigation_more -> {
                    val fragment = MoreFragment.Companion.newInstance()
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
            .setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
            .replace(R.id.content, fragment, fragment.javaClass.getSimpleName())
            .addToBackStack(fragment.javaClass.getSimpleName())
            .commit()
    }

}
