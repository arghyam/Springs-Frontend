package com.arghyam.landing.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.arghyam.R
import com.arghyam.commons.LocationInterface
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants.LOCATION_PERMISSION_NOT_GRANTED
import com.arghyam.commons.utils.Constants.PERMISSION_LOCATION_RESULT_CODE
import com.arghyam.commons.utils.Constants.TAG_HOME
import com.arghyam.commons.utils.Constants.TAG_MORE
import com.arghyam.commons.utils.Constants.TAG_MY_ACTIVITY
import com.arghyam.commons.utils.Constants.TAG_SEARCH
import com.arghyam.landing.interfaces.PermissionInterface
import com.arghyam.landing.ui.fragment.ErrorFragment
import com.arghyam.landing.ui.fragment.HomeFragment
import com.arghyam.more.ui.MoreFragment
import com.arghyam.myactivity.ui.MyActivityFragment
import com.arghyam.search.ui.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class LandingActivity : AppCompatActivity(), PermissionInterface, LocationInterface {

    var CURRENT_TAG: String = TAG_HOME
    lateinit var navView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)
        navView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        showHome()
    }


    private fun showHome() {
        if (ArghyamUtils().permissionGranted(
                this@LandingActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val fragment = HomeFragment.newInstance()
            addFragment(fragment)
        } else {
            val fragment = ErrorFragment.newInstance()
            addFragment(fragment)
        }
    }

    private val onNavigationItemSelectedListener = object : BottomNavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.navigation_home -> {
                    CURRENT_TAG = TAG_HOME
                    showHome()
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
            .addToBackStack(fragment.javaClass.simpleName)
            .commit()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        if (CURRENT_TAG == TAG_HOME) {
            finish()
        } else {
            CURRENT_TAG = TAG_HOME
            navView.menu[0].isChecked = true
            showHome()
        }
    }

    override fun permissionClick() {
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(permissionListener).check()
    }

    private val permissionListener = object : PermissionListener {
        override fun onPermissionGranted(response: PermissionGrantedResponse) {
            showHome()
            if (!ArghyamUtils().isLocationEnabled(this@LandingActivity)) {
                ArghyamUtils().turnOnLocation(this@LandingActivity)
            }

        }

        override fun onPermissionDenied(response: PermissionDeniedResponse) {
            if (response.isPermanentlyDenied) {
                ArghyamUtils().longToast(this@LandingActivity, LOCATION_PERMISSION_NOT_GRANTED)
                ArghyamUtils().openSettings(this@LandingActivity)
            }
        }

        override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
            token.continuePermissionRequest()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PERMISSION_LOCATION_RESULT_CODE -> showHome()
        }
    }

    override fun turnedLocationOn() {
        HomeFragment().getRecyclerView()
    }

}
