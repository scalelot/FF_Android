package com.festum.festumfield.Activity;

import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.festum.festumfield.Adapter.NotificationAdapter;
import com.festum.festumfield.BaseActivity;
import com.festum.festumfield.MainActivity;
import com.festum.festumfield.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MarketingNotificationActivity extends BaseActivity {

    RecyclerView recycler_noti;
    NotificationManagerCompat notificationManager;
    String[] txt_userName = {"Hello World", "Hello World", "Hello World", "Hello World", "Hello World"};
    ImageView hp_back_arrow;
    ArrayList<String> arraylist = new ArrayList<String>();
    CircleImageView img_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketing_notification);

        recycler_noti = findViewById(R.id.recycler_noti);
        hp_back_arrow = findViewById(R.id.hp_back_arrow);
        img_user = findViewById(R.id.img_user);

        int adPos = 0;
        for (int i = 0; i < txt_userName.length; i++) {
            if(adPos == 1) {
                arraylist.add(null);
                adPos=0;
            }
            arraylist.add(txt_userName[i]);
            adPos++;
        }

        notificationManager = NotificationManagerCompat.from(MarketingNotificationActivity.this);

        NotificationAdapter notificationAdapter = new NotificationAdapter(this, arraylist);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_noti.setLayoutManager(linearLayoutManager);
        recycler_noti.setAdapter(notificationAdapter);

        hp_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MarketingNotificationActivity.this,GroupProfileDetalisActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(MarketingNotificationActivity.this, MainActivity.class));
        finish();
    }
}