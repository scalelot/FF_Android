package com.festum.festumfield.Activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.festum.festumfield.Adapter.LikeAndCommentAdapter;
import com.festum.festumfield.BaseActivity;
import com.example.friendfield.R;

import java.util.ArrayList;

public class LikeAndCommentActivity extends BaseActivity {
    ImageView ic_back;
    TextView txt_title;
    RecyclerView recyclerview_like;
    String[] user_name = {"John Bryan", "Bryan", "Hunter Bryan", "Doris Collins", "Deann Sumpter", "John Bryan", "John Bryan", "John Bryan", "Deann Sumpter", "Collins Bryan", "John Bryan", "Deann Sumpter", "Collins Bryan"};
    LikeAndCommentAdapter likeAndCommentAdapter;
    ArrayList<String> arraylist = new ArrayList<String>();
    ArrayList<Integer> imagearraylist = new ArrayList<Integer>();
    private static final int LIST_AD_DELTA = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_and_comment);

        ic_back = findViewById(R.id.ic_back);
        txt_title = findViewById(R.id.txt_title);
        recyclerview_like = findViewById(R.id.recyclerview_like);

        int adPos = 0;
        for (int i = 0; i < user_name.length; i++) {
            if(adPos == 5) {
                arraylist.add(null);
                adPos=0;
            }
            arraylist.add(user_name[i]);
            adPos++;
        }


        recyclerview_like.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        likeAndCommentAdapter = new LikeAndCommentAdapter(this, arraylist);
        recyclerview_like.setAdapter(likeAndCommentAdapter);

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    @Override
    public void onBackPressed() {
        finish();
    }
}