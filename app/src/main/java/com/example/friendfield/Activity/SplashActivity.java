package com.example.friendfield.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.friendfield.BaseActivity;
import com.example.friendfield.MainActivity;
import com.example.friendfield.MyApplication;
import com.example.friendfield.R;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (MyApplication.getAuthToken(getApplicationContext()).isEmpty()) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            }
        }, 4000);
    }
}