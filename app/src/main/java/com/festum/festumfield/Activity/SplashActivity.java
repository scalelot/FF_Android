package com.festum.festumfield.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.festum.festumfield.BaseActivity;
import com.festum.festumfield.MainActivity;
import com.festum.festumfield.MyApplication;
import com.festum.festumfield.R;
import com.festum.festumfield.verstion.firstmodule.screens.main.HomeActivity;
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates;

import dagger.hilt.android.AndroidEntryPoint;


public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AppPreferencesDelegates.Companion.get().getToken().isEmpty()) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                } else {
//                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                }
                finish();
            }
        }, 1000);
    }
}