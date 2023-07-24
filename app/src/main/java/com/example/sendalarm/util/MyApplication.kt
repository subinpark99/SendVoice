package com.example.sendalarm.util

import android.app.Application

class MyApplication : Application() {
    companion object {
        lateinit var prefs: Preference
    }

    override fun onCreate() {
        prefs = Preference(applicationContext)
        super.onCreate()
    }
}