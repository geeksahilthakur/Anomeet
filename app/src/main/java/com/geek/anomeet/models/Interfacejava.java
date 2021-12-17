package com.geek.anomeet.models;

import android.webkit.JavascriptInterface;

import com.geek.anomeet.activities.CallActivity;

public class Interfacejava {

    CallActivity callActivity;

    public Interfacejava(CallActivity callActivity) {
        this.callActivity = callActivity;

    }

    @JavascriptInterface
    public void onPeerConnected() {
        callActivity.PeerConnected();

    }


}
