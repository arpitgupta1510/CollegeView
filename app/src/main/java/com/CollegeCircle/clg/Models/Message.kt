package com.CollegeCircle.clg.Models


data class Message (
    var image: String? = null,
    var msg: String? = null,
    var senderId: String? = null,
    var repliedTo:String?=null,
    var date: String? = null,
    var seen: Boolean = false,
    var msgId: String? = null
)