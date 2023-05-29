package com.festum.festumfield.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.festum.festumfield.BaseActivity;
import com.festum.festumfield.MainActivity;
import com.festum.festumfield.MyApplication;
import com.festum.festumfield.R;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SplashActivity extends BaseActivity {

    Socket mSocket;
    MyApplication myApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if (MyApplication.getInstance().isNightModeEnabled()) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        myApplication = new MyApplication();



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

    public void getMessageRecive() {
        try {
            mSocket.on("newMessage", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("GetMessageData:="+args);
                }
            });
        } catch (Exception e) {
            Log.e("Exception:==",e.toString());
        }
    }
}