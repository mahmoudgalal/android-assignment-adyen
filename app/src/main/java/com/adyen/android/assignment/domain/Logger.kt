package com.adyen.android.assignment.domain

interface Logger {
    fun log(channelName: String, message: String)
}