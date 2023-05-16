package com.festum.festumfield.Activity;

import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.festum.festumfield.BaseActivity;
import com.festum.festumfield.R;

public class PromotionPlanActivity extends BaseActivity {

    ImageView ic_back;
    AppCompatButton btn_create_notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_plan);

        ic_back = findViewById(R.id.ic_back);
        btn_create_notification = findViewById(R.id.btn_create_notification);

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_create_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PromotionPlanActivity.this, ChooseUserPromotionActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PromotionPlanActivity.this,PromotionActivity.class));
        finish();
    }
}