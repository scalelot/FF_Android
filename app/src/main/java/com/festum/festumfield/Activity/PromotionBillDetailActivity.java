package com.festum.festumfield.Activity;

import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.festum.festumfield.BaseActivity;
import com.festum.festumfield.R;

public class PromotionBillDetailActivity extends BaseActivity {
    AppCompatButton button_ok;
    TextView publish_date_time;
    ImageView back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_bill_detail);

        button_ok = findViewById(R.id.button_ok);
        publish_date_time = findViewById(R.id.publish_date_time);
        back_btn = findViewById(R.id.back_btn);

        String publish_date = getSharedPreferences("Datetime", MODE_PRIVATE).getString("date_time", null);
        publish_date_time.setText(publish_date);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PromotionBillDetailActivity.this, PromotionActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PromotionBillDetailActivity.this, PaymentSuccessfulActivity.class));
        finish();
    }
}