package com.festum.festumfield.Activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.festum.festumfield.BaseActivity;
import com.festum.festumfield.R;
import com.festum.festumfield.verstion.firstmodule.screens.main.ApplicationPermissionActivity;
import com.festum.festumfield.verstion.firstmodule.screens.main.HomeActivity;
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates;

import java.util.ArrayList;
import java.util.List;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {

    public String callId, messageId, fromId, toId, toUserName, banner;

    public static String[] storge_permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storge_permissions_33 = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS};

    List<String> listPermissionsNeeded = new ArrayList<>();
    String perStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            messageId = extras.getString("messageid", "");

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();


        banner = (extras != null) ? extras.getString("banner", "") : "";
        callId = (extras != null) ? extras.getString("callid", "") : "";
        messageId = (extras != null) ? extras.getString("messageid", "") : "";
        fromId = (extras != null) ? extras.getString("fromId", "") : "";
        toId = (extras != null) ? extras.getString("toId", "") : "";
        toUserName = (extras != null) ? extras.getString("toUserName", "") : "";

//        }

        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AppPreferencesDelegates.Companion.get().getToken().isEmpty()) {

                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));

                } else {

                    checkPermissions();

                }
            }
        }, 1000);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));

                } else {
                    for (String per : permissions) {
                        perStr += "\n" + per;
                    }
                }
            }
        }
    }

    private boolean checkPermissions() {
        int result = 0;

        listPermissionsNeeded.clear();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            for (String p : storge_permissions_33) {
                result = ContextCompat.checkSelfPermission(getApplicationContext(), p);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(p);

                }
            }
        } else {
            for (String p : storge_permissions) {
                result = ContextCompat.checkSelfPermission(getApplicationContext(), p);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(p);

                }
            }
        }
        Log.e("checkPermissions:", String.valueOf(listPermissionsNeeded));
        if (!listPermissionsNeeded.isEmpty()) {
            startActivity(new Intent(SplashActivity.this, ApplicationPermissionActivity.class));
            return false;
        } else {


            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            intent.putExtra("callId", callId);
            intent.putExtra("banner",banner );
            intent.putExtra("messageId", messageId);
            intent.putExtra("fromId", fromId);
            intent.putExtra("toId", toId);
            intent.putExtra("toId", toId);
            startActivity(intent);

            /*if (!callId.isEmpty()){

                Intent intent = new Intent();
                intent.putExtra("callId",callId);
                intent.putExtra("fromId",fromId);
                intent.putExtra("toId",toId);
                intent.putExtra("fromId",fromId);
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));

            }else {

            }*/

        }
        return true;
    }
}