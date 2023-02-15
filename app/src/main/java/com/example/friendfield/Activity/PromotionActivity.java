package com.example.friendfield.Activity;

import androidx.core.app.NotificationManagerCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.friendfield.BaseActivity;
import com.example.friendfield.Fragment.HistoryPromotionFragment;
import com.example.friendfield.Fragment.RunningPromotionFragment;
import com.example.friendfield.MainActivity;
import com.example.friendfield.R;

public class PromotionActivity extends BaseActivity {


    TextView running, history, send_request, select;
    ColorStateList def;
    ImageView ic_back;
    NotificationManagerCompat notificationManager;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion);

        running = findViewById(R.id.running);
        history = findViewById(R.id.history);
        select = findViewById(R.id.select);
        ic_back = findViewById(R.id.ic_back);

        notificationManager = NotificationManagerCompat.from(PromotionActivity.this);

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        def = history.getTextColors();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.framlayout,
                        new RunningPromotionFragment()).commit();

        running.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running.setTextColor(getResources().getColor(R.color.app_color));
                history.setTextColor(def);
                select.animate().x(0).setDuration(100);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framlayout,
                                new RunningPromotionFragment()).commit();
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                running.setTextColor(def);
                history.setTextColor(getResources().getColor(R.color.app_color));
                int size = history.getWidth();
                select.animate().x(size).setDuration(100);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framlayout,
                                new HistoryPromotionFragment()).commit();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PromotionActivity.this, MainActivity.class));
        finish();
    }
}