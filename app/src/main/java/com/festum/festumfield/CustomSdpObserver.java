package com.festum.festumfield;

import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

public class CustomSdpObserver implements SdpObserver {
    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {
        // Called when SDP creation is successful.
    }

    @Override
    public void onSetSuccess() {
        // Called when setting the local or remote description is successful.
    }

    @Override
    public void onCreateFailure(String s) {
        // Called when SDP creation is failed.
    }

    @Override
    public void onSetFailure(String s) {
        // Called when setting the local or remote description is failed.
    }
}