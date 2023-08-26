package com.festum.festumfield;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.festum.festumfield.Utils.Constans;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import dagger.hilt.android.HiltAndroidApp;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MyApplication {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;

    public MyApplication(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("myPref",Context.MODE_PRIVATE);
    }

    public void getUserName(String code){
        editor = sharedPreferences.edit();
        editor.putString("countryCode",code);
        editor.apply();
    }

    String setUserName(){
        return sharedPreferences.getString("countryCode","");
    }

    public static void setuserName(Context context, String contactNo) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if (null == sharedPreferences) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", contactNo);
        editor.commit();
    }

    public static String getuserName(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String cookie = sharedPreferences.getString("userName", "");
        return cookie;
    }

    public static void setAuthToken(Context context, String authToken) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AuthTokens", MODE_PRIVATE);
        if (null == sharedPreferences) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("AuthToken", authToken);
        editor.commit();
    }

    public static String getAuthToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AuthTokens", MODE_PRIVATE);
        String cookie = sharedPreferences.getString("AuthToken", "");
        return cookie;
    }

    public static void setChannelId(Context context, String channelId) {
        SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if (sharedPreferences1 == null) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences1.edit();
        editor.putString("ChannelIds", channelId);
        editor.commit();
    }

    public static String getChannelId(Context context) {
        SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String str = sharedPreferences1.getString("ChannelIds", "");
        return str;
    }

    public static void setOnlineId(Context context, String channelId) {
        SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if (sharedPreferences1 == null) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences1.edit();
        editor.putString("onlineId", channelId);
        editor.commit();
    }

    public static String getOnlineId(Context context) {
        SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String str = sharedPreferences1.getString("onlineId", "");
        return str;
    }

    public static void setBusinessProfileRegistered(Context context, Boolean useractive) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if (null == sharedPreferences) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isBusinessProfileRegistered", useractive);
        editor.apply();
    }

    public static Boolean isBusinessProfileRegistered(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        Boolean useractive = sharedPreferences.getBoolean("isBusinessProfileRegistered", false);

        return useractive;
    }

    public static Boolean isPersonalProfileRegistered(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        Boolean useractive = sharedPreferences.getBoolean("isPersonalProfileRegistered", false);

        return useractive;
    }

    public static void setNotificationToken(Context context, String newToken) {
        SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences1.edit();
        editor.putString("newToken", newToken);
        editor.apply();
        editor.commit();
    }

    public static String getNotificationToken(Context context) {
        SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String newTokenGet = sharedPreferences1.getString("newToken", "");
        return newTokenGet;
    }
}
