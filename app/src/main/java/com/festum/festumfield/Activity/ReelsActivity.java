package com.festum.festumfield.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.festum.festumfield.Adapter.ReelsVideoAdapter;
import com.festum.festumfield.Model.ReelsModel;
import com.festum.festumfield.R;

import java.util.ArrayList;
import java.util.List;

public class ReelsActivity extends AppCompatActivity {

    ImageView upload_reels_video, ic_back;
    ViewPager2 viewPager2;
    String[] name = {"Hello", "World", "Welcome", "Hunter", "Bryan", "Hello", "World", "Welcome", "Hunter", "Bryan"};
    ReelsVideoAdapter reelsVideoAdapter;
    List<ReelsModel> reelsModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reels);

        viewPager2 = findViewById(R.id.viewPager2);
        upload_reels_video = findViewById(R.id.upload_reels_video);
        ic_back = findViewById(R.id.ic_back);

        for (int i = 0; i < name.length; i++) {
            ReelsModel reelsModel = new ReelsModel();
            reelsModel.setName(name[i]);

            reelsModelList.add(reelsModel);
        }

        reelsVideoAdapter = new ReelsVideoAdapter(reelsModelList);
        viewPager2.setAdapter(reelsVideoAdapter);

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        upload_reels_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReelsActivity.this, CreateReelsActivity.class));
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        /*startActivity(new Intent(ReelsActivity.this, MainActivity.class));*/
        finish();
    }
}