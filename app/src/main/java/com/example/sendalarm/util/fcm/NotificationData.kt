package com.example.sendalarm.util.fcm

data class NotificationData(
    var title: String,
    var body: String,
    var familyId: Int
)