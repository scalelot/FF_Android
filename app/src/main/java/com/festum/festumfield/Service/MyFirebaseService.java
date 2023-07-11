package com.festum.festumfield.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.festum.festumfield.MyApplication;
import com.festum.festumfield.R;
import com.festum.festumfield.Utils.Utilities;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseService extends FirebaseMessagingService {

    String channelId;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.e(Utilities.TAG, token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        try {
            channelId = MyApplication.getChannelId(this);

            if (message.getNotification().getImageUrl() != null){
                String img = String.valueOf(message.getNotification().getImageUrl());
                System.out.println("img="+img);
            }
            if (message.getNotification().getTitle() != null) {
                String title = message.getNotification().getTitle();
                String body = message.getNotification().getBody();


                NotificationChannel channel = new NotificationChannel(channelId, "Message Notification", NotificationManager.IMPORTANCE_HIGH);
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.createNotificationChannel(channel);
                channel.enableLights(true);

                Notification notification = new Notification.Builder(this, channelId).
                        setContentTitle(title).
                        setContentText(body).
                        setSmallIcon(R.mipmap.ic_app_logo).
                        setAutoCancel(true).
                            build();

                manager.notify(1, notification);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}