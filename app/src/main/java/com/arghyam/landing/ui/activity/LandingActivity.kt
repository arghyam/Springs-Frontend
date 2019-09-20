package com.arghyam.landing.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.arghyam.R
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.Constants
import com.arghyam.commons.utils.Constants.LOCATION_PERMISSION_NOT_GRANTED
import com.arghyam.commons.utils.Constants.PERMISSION_LOCATION_ON_RESULT_CODE
import com.arghyam.commons.utils.Constants.PERMISSION_LOCATION_RESULT_CODE
import com.arghyam.commons.utils.Constants.TAG_FAVOURITES
import com.arghyam.commons.utils.Constants.TAG_HOME
import com.arghyam.commons.utils.Constants.TAG_MORE
import com.arghyam.commons.utils.Constants.TAG_MY_ACTIVITY
import com.arghyam.commons.utils.Constants.TAG_SEARCH
import com.arghyam.commons.utils.SharedPreferenceFactory
import com.arghyam.favourites.ui.FavouritesFragment
import com.arghyam.iam.ui.LoginActivity
import com.arghyam.landing.interfaces.PermissionInterface
import com.arghyam.landing.ui.fragment.ErrorFragment
import com.arghyam.landing.ui.fragment.HomeFragment
import com.arghyam.landing.viewmodel.LandingViewModel
import com.arghyam.more.ui.MoreFragment
import com.arghyam.myactivity.ui.MyActivityFragment
import com.arghyam.search.interfaces.NavigationInterface
import com.arghyam.search.ui.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener


class LandingActivity : AppCompatActivity(), PermissionInterface {

//        if(savedInstanceState!=null){
//            mContent = supportFragmentManager.getFragment(savedInstanceState, "HomeFragment")
//        }
    var landingViewModel: LandingViewModel? = null
    var CURRENT_TAG: String = TAG_HOME
    var isAccepted: Boolean = false
    var mContent: Fragment? = null
    private var notification: Boolean = true
    lateinit var navView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

//        if(savedInstanceState!=null){
//            mContent = supportFragmentManager.getFragment(savedInstanceState, "HomeFragment")
//        }
        navView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        initViewModel()
        showHome()
    }

    private fun initViewModel() {
        landingViewModel = ViewModelProviders.of(this).get(LandingViewModel::class.java)
    }
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
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

    fun switchHome(){
        showHome()
        navView.menu[0].isChecked = true
    }

    private val onNavigationItemSelectedListener = object : BottomNavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.navigation_home -> {
                    CURRENT_TAG = TAG_HOME
                    showHome()
                    return true
                }
                R.id.navigation_fav -> {

                    CURRENT_TAG = TAG_FAVOURITES
                    val fragment = FavouritesFragment.newInstance()
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
            .addToBackStack(fragment.javaClass.simpleName)
            .commit()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        if (CURRENT_TAG == TAG_HOME) {
            if (SharedPreferenceFactory(this@LandingActivity).getString(Constants.ACCESS_TOKEN) == "") {
                startActivity(Intent(this, LoginActivity::class.java))
            }
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
            isAccepted = true
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
        Log.e("karthik req", "" + requestCode)
        Log.e("karthik", "" + resultCode)

        when (requestCode) {
            PERMISSION_LOCATION_RESULT_CODE -> showHome()
            PERMISSION_LOCATION_ON_RESULT_CODE -> {
                when (resultCode) {
                    Constants.GPS_ENABLED -> {
                        landingViewModel?.checkGpsStatus(resultCode)
                    }
                    Constants.GPS_DISABLED -> {
                        ArghyamUtils().turnOnLocation(this@LandingActivity)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isAccepted) {
            showHome()
        }

    }
    private var navigationInterface: NavigationInterface = object : NavigationInterface {
        override fun backButtonClickListener() {
            showHome()
            navView.menu[0].isChecked = true
        }
    }
}
