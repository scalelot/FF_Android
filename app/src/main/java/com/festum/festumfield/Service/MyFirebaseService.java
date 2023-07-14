package com.festum.festumfield.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.festum.festumfield.MyApplication;
import com.festum.festumfield.R;
import com.festum.festumfield.Utils.Constans;
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
            Log.e("onMessageReceived:=", message.getData().toString());
            if (message.getNotification().getTitle() != null) {
                String title = message.getNotification().getTitle();
                String txtMessage = message.getData().get("text");
                String imageUri = message.getData().get("media_path");
                String productName = message.getData().get("product_name");
                String productImg = message.getData().get("product_image");

                NotificationChannel channel = new NotificationChannel(channelId, "Message Notification", NotificationManager.IMPORTANCE_HIGH);
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.createNotificationChannel(channel);
                channel.enableLights(true);

                NotificationCompat.Builder notification = null;
                if (!productImg.isEmpty()) {
                    NotificationCompat.BigPictureStyle pictureStyle1 = new NotificationCompat.BigPictureStyle();
                    pictureStyle1.setBigContentTitle(title);
                    pictureStyle1.bigPicture(Glide.with(this).asBitmap().load(Constans.Display_Image_URL + productImg).submit().get());

                    notification = new NotificationCompat.Builder(this, channelId).setContentTitle(productName).setContentText(txtMessage).setSmallIcon(R.drawable.noti_icon).setAutoCancel(true).setStyle(pictureStyle1);
                } else if (!imageUri.isEmpty()) {
                    NotificationCompat.BigPictureStyle pictureStyle = new NotificationCompat.BigPictureStyle();
                    pictureStyle.setBigContentTitle(title);
                    pictureStyle.bigPicture(Glide.with(this).asBitmap().load(imageUri).submit().get());

                    notification = new NotificationCompat.Builder(this, channelId)
                            .setContentTitle(title)
                            .setSmallIcon(R.drawable.noti_icon)
                            .setAutoCancel(true)
                            .setStyle(pictureStyle);
                } else {
                    notification = new NotificationCompat.Builder(this, channelId)
                            .setContentTitle(title)
                            .setContentText(txtMessage)
                            .setSmallIcon(R.drawable.noti_icon)
                            .setAutoCancel(true);
                }

                manager.notify(1, notification.build());
            } else {
                Log.e("onMessageReceived:=", "Error Notification Show");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}