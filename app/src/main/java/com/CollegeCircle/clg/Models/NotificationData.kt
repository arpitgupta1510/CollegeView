package com.CollegeCircle.clg.Models

data class NotificationData(
    val notificationId:String?=null,
    val senderId: String?=null,
    val message: String?=null,
    val receiverId:String?=null,
    val callId:String?=null,
)
