package com.example.sendalarm.data.entity

import com.example.sendalarm.util.fcm.NotificationData

data class PushNotification(
    var data: NotificationData,
    var to: String
)