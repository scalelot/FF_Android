package com.example.friendfield.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.friendfield.BaseActivity;
import com.example.friendfield.R;

public class PaymentActivity extends BaseActivity {

    ImageView ic_back_btn;
    AppCompatButton button_pay_now;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        ic_back_btn = findViewById(R.id.ic_back_btn);
        button_pay_now = findViewById(R.id.button_pay_now);

        ic_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        button_pay_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PaymentActivity.this,PaymentSuccessfulActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PaymentActivity.this,PromotionBillActivity.class));
        finish();
    }
}