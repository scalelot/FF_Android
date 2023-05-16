package com.festum.festumfield.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.friendfield.R;

public class ReelsPerviewActivity extends AppCompatActivity {

    MediaController mediaController;
    ImageView back_arrow;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reels_perview);

        String urip = getSharedPreferences("Reels", MODE_PRIVATE).getString("uri", null);

        System.out.println("Uri:----"+urip);
        Log.e("Uri",urip);

        back_arrow = findViewById(R.id.back_arrow);
        videoView = findViewById(R.id.videoView);

        mediaController = new MediaController(this);
        if (urip != null) {
            videoView.setVideoURI(Uri.parse(urip));
            videoView.requestFocus();
            videoView.setMediaController(mediaController);
            mediaController.setAnchorView(videoView);
            videoView.start();
        }

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ReelsPerviewActivity.this, CreateReelsActivity.class));
        finish();
    }
}