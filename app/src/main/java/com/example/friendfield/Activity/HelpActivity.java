package com.example.friendfield.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.friendfield.BaseActivity;
import com.example.friendfield.R;

public class HelpActivity extends BaseActivity {

    RelativeLayout rl_faq1, rl_faq2, rl_faq3, rl_faq4, rl_faq5, rl_faq6;
    ImageView hp_back_arrow,img_top, img_top2, img_top3, img_top4, img_top5, img_top6, img_bottom, img_bottom2, img_bottom3, img_bottom4, img_bottom5, img_bottom6;
    TextView txt_dis, txt_dis2, txt_dis3, txt_dis4, txt_dis5, txt_dis6;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        hp_back_arrow = findViewById(R.id.hp_back_arrow);
        rl_faq1 = findViewById(R.id.rl_faq1);
        rl_faq2 = findViewById(R.id.rl_faq2);
        rl_faq3 = findViewById(R.id.rl_faq3);
        rl_faq4 = findViewById(R.id.rl_faq4);
        rl_faq5 = findViewById(R.id.rl_faq5);
        rl_faq6 = findViewById(R.id.rl_faq6);
        img_top = findViewById(R.id.img_top);
        img_top2 = findViewById(R.id.img_top2);
        img_top3 = findViewById(R.id.img_top3);
        img_top4 = findViewById(R.id.img_top4);
        img_top5 = findViewById(R.id.img_top5);
        img_top6 = findViewById(R.id.img_top6);
        img_bottom = findViewById(R.id.img_bottom);
        img_bottom2 = findViewById(R.id.img_bottom2);
        img_bottom3 = findViewById(R.id.img_bottom3);
        img_bottom4 = findViewById(R.id.img_bottom4);
        img_bottom5 = findViewById(R.id.img_bottom5);
        img_bottom6 = findViewById(R.id.img_bottom6);
        txt_dis = findViewById(R.id.txt_dis);
        txt_dis2 = findViewById(R.id.txt_dis2);
        txt_dis3 = findViewById(R.id.txt_dis3);
        txt_dis4 = findViewById(R.id.txt_dis4);
        txt_dis5 = findViewById(R.id.txt_dis5);
        txt_dis6 = findViewById(R.id.txt_dis6);

        hp_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        rl_faq1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i % 2 == 0) {
                    txt_dis.setVisibility(View.GONE);
                    img_top.setVisibility(View.GONE);
                    img_bottom.setVisibility(View.VISIBLE);
                } else {
                    txt_dis.setVisibility(View.VISIBLE);
                    img_top.setVisibility(View.VISIBLE);
                    img_bottom.setVisibility(View.GONE);
                }
                i++;
            }
        });

        rl_faq2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i % 2 == 0) {
                    txt_dis2.setVisibility(View.VISIBLE);
                    img_top2.setVisibility(View.VISIBLE);
                    img_bottom2.setVisibility(View.GONE);
                } else {
                    txt_dis2.setVisibility(View.GONE);
                    img_top2.setVisibility(View.GONE);
                    img_bottom2.setVisibility(View.VISIBLE);
                }
                i++;
            }
        });

        rl_faq3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i % 2 == 0) {
                    txt_dis3.setVisibility(View.VISIBLE);
                    img_top3.setVisibility(View.VISIBLE);
                    img_bottom3.setVisibility(View.GONE);
                } else {
                    txt_dis3.setVisibility(View.GONE);
                    img_top3.setVisibility(View.GONE);
                    img_bottom3.setVisibility(View.VISIBLE);
                }
                i++;
            }
        });

        rl_faq4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i % 2 == 0) {
                    txt_dis4.setVisibility(View.VISIBLE);
                    img_top4.setVisibility(View.VISIBLE);
                    img_bottom4.setVisibility(View.GONE);
                } else {
                    txt_dis4.setVisibility(View.GONE);
                    img_top4.setVisibility(View.GONE);
                    img_bottom4.setVisibility(View.VISIBLE);
                }
                i++;
            }
        });

        rl_faq5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i % 2 == 0) {
                    txt_dis5.setVisibility(View.VISIBLE);
                    img_top5.setVisibility(View.VISIBLE);
                    img_bottom5.setVisibility(View.GONE);
                } else {
                    txt_dis5.setVisibility(View.GONE);
                    img_top5.setVisibility(View.GONE);
                    img_bottom5.setVisibility(View.VISIBLE);
                }
                i++;
            }
        });

        rl_faq6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i % 2 == 0) {
                    txt_dis6.setVisibility(View.VISIBLE);
                    img_top6.setVisibility(View.VISIBLE);
                    img_bottom6.setVisibility(View.GONE);
                } else {
                    txt_dis6.setVisibility(View.GONE);
                    img_top6.setVisibility(View.GONE);
                    img_bottom6.setVisibility(View.VISIBLE);
                }
                i++;
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(HelpActivity.this,SettingActivity.class));
        finish();
    }
}