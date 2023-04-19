package com.example.friendfield.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.friendfield.BaseActivity;
import com.example.friendfield.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChattingAudioCallActivity extends BaseActivity {

    ImageView ic_back_arrow;
    CircleImageView call_user_img;
    TextView call_user_name, call_user_status;
    LinearLayout ll_videoCall, ll_sound, ll_mute, ll_call_cut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_audio_call);

        ic_back_arrow = findViewById(R.id.ic_back_arrow);
        call_user_img = findViewById(R.id.call_user_img);
        call_user_name = findViewById(R.id.call_user_name);
        call_user_status = findViewById(R.id.call_user_status);
        ll_videoCall = findViewById(R.id.ll_videoCall);
        ll_sound = findViewById(R.id.ll_sound);
        ll_mute = findViewById(R.id.ll_mute);
        ll_call_cut = findViewById(R.id.ll_call_cut);

        ic_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ll_videoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChattingAudioCallActivity.this, "Video Calling", Toast.LENGTH_SHORT).show();
            }
        });

        ll_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChattingAudioCallActivity.this, "Video Sound", Toast.LENGTH_SHORT).show();
            }
        });

        ll_mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChattingAudioCallActivity.this, "Call Mute", Toast.LENGTH_SHORT).show();
            }
        });

        ll_call_cut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChattingAudioCallActivity.this, "Call Cut", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ChattingAudioCallActivity.this,ChatingActivity.class));
        finish();
    }
}