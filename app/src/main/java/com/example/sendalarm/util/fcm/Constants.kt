package com.example.sendalarm.util.fcm

import com.example.sendalarm.BuildConfig


class Constants {
    companion object {
        const val BASE_URL = "https://fcm.googleapis.com"
        const val SERVER_KEY = BuildConfig.FCM_SERVER_KEY
        const val CONTENT_TYPE = "application/json"
    }
}