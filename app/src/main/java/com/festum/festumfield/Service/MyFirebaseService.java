package com.festum.festumfield.Service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.festum.festumfield.MyApplication;
import com.festum.festumfield.Utils.Utilities;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseService extends FirebaseMessagingService {

//    String channelId = MyApplication.getChannelId(getApplicationContext());

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.e(Utilities.TAG, token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        Map<String, String> data = message.getData();

        // Loop through the data payload and print the values.
        for (String key : data.keySet()) {
            Log.d("dataRecive:--", "Key: " + key + ", Value: " + data.get(key));
        }

//        Log.d("recive", message.getData().toString());
    }
}