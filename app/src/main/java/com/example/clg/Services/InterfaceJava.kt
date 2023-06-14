package com.example.clg.Services

import android.webkit.JavascriptInterface
import com.example.clg.Activities.CallActivity


class InterfaceJava(var callActivity: CallActivity) {
    @JavascriptInterface
    fun onPeerConnected() {
        callActivity.onPeerConnected()
    }
}