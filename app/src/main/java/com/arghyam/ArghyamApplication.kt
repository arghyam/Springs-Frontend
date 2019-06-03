package com.arghyam

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.arghyam.commons.di.AppComponent
import com.arghyam.commons.di.AppModule
import com.arghyam.commons.di.DaggerAppComponent
import com.arghyam.commons.interfaces.NetworkStateReceiverListener
import com.arghyam.commons.utils.ArghyamUtils
import com.arghyam.commons.utils.NetworkStateReceiver


class ArghyamApplication : Application(), Application.ActivityLifecycleCallbacks, NetworkStateReceiverListener {


    private var mAppComponent: AppComponent? = null
    private var activityReferences = 0
    private var isActivityChangingConfigurations = false
    private var networkStateReceiver: NetworkStateReceiver? = null
    private var isOffline: Boolean = false

    private val appModule: AppModule
        get() = AppModule(this)

    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        registerActivityLifecycleCallbacks(this)
        mAppComponent = DaggerAppComponent.builder()
            .appModule(appModule)
            .build()
        networkStateReceiver = NetworkStateReceiver()
        networkStateReceiver?.addListener(this)
        this.registerReceiver(networkStateReceiver, IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION))
    }


    fun getmAppComponent(): AppComponent? {
        return mAppComponent
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
        if (++activityReferences == 1 && !isActivityChangingConfigurations) {
            Log.d("Application====", "in foreground")
        }
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {
        isActivityChangingConfigurations = activity.isChangingConfigurations
        if (--activityReferences == 0 && !isActivityChangingConfigurations) {
            Log.d("Application====", "in background")

        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
//        networkStateReceiver?.removeListener(this);
//        this.unregisterReceiver(networkStateReceiver);
    }

    override fun attachBaseContext(base: Context) {
        var base = base
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val config = base.resources.configuration
            base = base.createConfigurationContext(config)
        }
        super.attachBaseContext(base)
    }


    override fun networkAvailable() {
        if (isOffline) {
            ArghyamUtils().longToast(applicationContext, "Network Available")
            isOffline = false
        }
    }

    override fun networkUnavailable() {
        ArghyamUtils().longToast(applicationContext, "Poor network connection detected. Please check your connectivity")
        isOffline = true
    }

}
