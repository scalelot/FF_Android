package com.festum.festumfield.Activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.festum.festumfield.BaseActivity;
import com.festum.festumfield.Fragment.FriendRequestFragment;
import com.festum.festumfield.Fragment.SendRequestFragment;
import com.festum.festumfield.MainActivity;
import com.example.friendfield.R;

public class RequestActivity extends BaseActivity {

    ImageView ic_back;
    TextView friend_request,send_request,select;
    ColorStateList def;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        ic_back = findViewById(R.id.ic_back);
        friend_request = findViewById(R.id.friend_request);
        send_request = findViewById(R.id.send_request);
        select = findViewById(R.id.select);

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        def = send_request.getTextColors();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fram_layout,
                        new FriendRequestFragment()).commit();

        friend_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friend_request.setTextColor(getResources().getColor(R.color.app_color));
                send_request.setTextColor(def);
                select.animate().x(0).setDuration(100);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fram_layout,
                                new FriendRequestFragment()).commit();
            }
        });

        send_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friend_request.setTextColor(def);
                send_request.setTextColor(getResources().getColor(R.color.app_color));
                int size = send_request.getWidth();
                select.animate().x(size).setDuration(100);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fram_layout,
                                new SendRequestFragment()).commit();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RequestActivity.this, MainActivity.class));
        finish();
    }
}