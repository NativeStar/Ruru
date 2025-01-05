package com.byxiaorun.detector

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle

/**
 *Created by byxiaorun on 2022/4/20/0020.
 */
class MyApplication : Application(),Application.ActivityLifecycleCallbacks {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var appContext: Context
        lateinit var topActivity:Activity
        lateinit var accList: List<String>
        lateinit var accountList: List<String>
        var accenable:Boolean = false
        var adbenable:Boolean = false
        var development_enable:Boolean = false
        var vpn_connect:Boolean = false

    }

    init {
        System.loadLibrary("applist_detector")
    }


    override fun onCreate() {
        super.onCreate()
        appContext = this
        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        topActivity=activity;
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}
