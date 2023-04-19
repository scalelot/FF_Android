package com.example.friendfield.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.friendfield.Adapter.ReelsAdapter;
import com.example.friendfield.Adapter.ReelsCommentAdapter;
import com.example.friendfield.Adapter.ReelsVideoAdapter;
import com.example.friendfield.Adapter.ViewPagerAdapter;
import com.example.friendfield.MainActivity;
import com.example.friendfield.Model.ReelsModel;
import com.example.friendfield.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

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
        startActivity(new Intent(ReelsActivity.this, MainActivity.class));
        finish();
    }
}