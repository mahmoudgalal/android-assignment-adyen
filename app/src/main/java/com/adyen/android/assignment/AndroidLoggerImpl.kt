package com.adyen.android.assignment

import android.util.Log
import com.adyen.android.assignment.domain.Logger

class AndroidLoggerImpl : Logger {
    override fun log(channelName: String, message: String) {
        Log.d(channelName, message)
    }
}