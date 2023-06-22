package com.CollegeCircle.clg.Services

import android.webkit.JavascriptInterface
import com.CollegeCircle.clg.Activities.CallActivity


class InterfaceJava(var callActivity: CallActivity) {
    @JavascriptInterface
    fun onPeerConnected() {
        callActivity.onPeerConnected()
    }
}