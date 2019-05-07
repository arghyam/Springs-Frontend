package com.arghyam

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.arghyam.commons.di.AppComponent
import com.arghyam.commons.di.AppModule
import com.arghyam.commons.di.DaggerAppComponent

class ArghyamApplication : Application(), Application.ActivityLifecycleCallbacks {

    private var mAppComponent: AppComponent? = null
    private var activityReferences = 0
    private var isActivityChangingConfigurations = false

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

    }

    override fun attachBaseContext(base: Context) {
        var base = base
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val config = base.resources.configuration
            base = base.createConfigurationContext(config)
        }
        super.attachBaseContext(base)
    }

}
