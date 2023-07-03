package com.festum.festumfield.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.festum.festumfield.MyApplication;
import com.festum.festumfield.R;

import java.util.Random;

public class Utilities {

    public static final String TAG = "FFApp";
    public static final String BASE_URL = "https://fcm.googleapi.com";
    public static final String SERVER_KEY = "AAAAGuWI7rs:APA91bEb24k9VcUmg5yrrUGAwc7zs_v6x3-iLczwtiMBRgadisd0LSKV3pSdOW6kY3vl0Gulj0Ou3QJD38T9C_qMplNdiTe6pbRglKm3adkDDCB88BAc8KMkobUu12JfV-64ySYPVUxo";
    public static final String CONTENT_TYPE ="application/json";

    public static void showNotification(Context context, String title, String body, PendingIntent pendingIntent) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MyApplication.getChannelId(context));

        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setContentIntent(pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(title);
        bigTextStyle.setBigContentTitle(title);
        bigTextStyle.setSummaryText(title);
        builder.setStyle(bigTextStyle);

        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(MyApplication.getChannelId(context), "chatMessage", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            builder.setChannelId(MyApplication.getChannelId(context));
        }

        manager.notify(new Random().nextInt(), builder.build());
    }

}
