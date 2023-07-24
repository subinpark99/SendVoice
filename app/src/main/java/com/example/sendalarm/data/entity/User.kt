package com.example.sendalarm.data.entity

data class User(
    var userId: String = "",
    var fcmToken: String = "",
    var userName: String = "",
    val email: String = "",
    var friendList: List<FriendList>? = null
)

data class FriendList(
    val friendUid: String,
    val friendName: String,
    val email: String
)

