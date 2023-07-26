package com.example.sendalarm.util

import android.app.Application
import com.example.sendalarm.util.kakao.KakaoSDKAdapter
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    companion object{
        var instance : MyApplication? = null
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        if( KakaoSDK.getAdapter() == null){
            KakaoSDK.init(KakaoSDKAdapter(getAppContext()))
        }
    }
}