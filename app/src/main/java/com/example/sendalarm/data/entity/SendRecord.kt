package com.example.sendalarm.data.entity

import java.io.File

data class SendRecord(
    var receiverId: String = "",
    var receiverName: String = "",
    var title: String = "",
    var file: File? = null,
    var time: String
)