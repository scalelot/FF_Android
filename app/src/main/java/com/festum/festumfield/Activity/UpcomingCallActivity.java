package com.festum.festumfield.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.festum.festumfield.BaseActivity;
import com.festum.festumfield.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpcomingCallActivity extends BaseActivity {

    ImageView ic_back;
    CircleImageView upcoingcall_user_img;
    TextView upcoming_username, upcoming_txt;
    LinearLayout ll_call_cut, ll_call_recive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_call);

        ic_back = findViewById(R.id.ic_back);
        upcoingcall_user_img = findViewById(R.id.upcomingcall_user_img);
        upcoming_username = findViewById(R.id.upcoming_username);
        upcoming_txt = findViewById(R.id.upcoming_txt);
        ll_call_cut = findViewById(R.id.ll_call_cut);
        ll_call_recive = findViewById(R.id.ll_call_recive);

        ll_call_cut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UpcomingCallActivity.this, "Call Cut", Toast.LENGTH_SHORT).show();
            }
        });

        ll_call_recive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UpcomingCallActivity.this, "Call Recive", Toast.LENGTH_SHORT).show();
            }
        });
    }
}