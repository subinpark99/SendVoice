package com.example.sendalarm.util

import android.app.Application
import com.example.sendalarm.BuildConfig
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Kakao Sdk 초기화
        KakaoSdk.init(this,BuildConfig.KAKAO_APP_KEY)
    }
}