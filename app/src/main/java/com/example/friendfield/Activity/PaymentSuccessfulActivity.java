package com.example.friendfield.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.friendfield.BaseActivity;
import com.example.friendfield.R;

public class PaymentSuccessfulActivity extends BaseActivity {

    AppCompatButton btn_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_successful);

        btn_ok = findViewById(R.id.btn_ok);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PaymentSuccessfulActivity.this,PromotionBillDetailActivity.class));
                finish();
            }
        });
    }
}