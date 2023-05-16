package com.festum.festumfield.Activity;

import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.festum.festumfield.BaseActivity;
import com.example.friendfield.R;

import java.text.DecimalFormat;

public class PromotionBillActivity extends BaseActivity {

    ImageView ic_back, img_apply;
    RelativeLayout rl_apply, rl_discount;
    AppCompatButton btn_pay_now;
    TextView txt_date_time, txt_save, txt_remove, txt_coupon_discout, text_noti;
    TextView noti_price, email_price, sms_price, total_price, discount_price, final_price;
    double n1, n2, n3, sum = 0, dis, ftotal;
    LinearLayout ll_save;
    DecimalFormat df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_bill);

        ic_back = findViewById(R.id.ic_back);
        rl_apply = findViewById(R.id.rl_apply);
        btn_pay_now = findViewById(R.id.btn_pay_now);
        txt_date_time = findViewById(R.id.txt_date_time);
        txt_save = findViewById(R.id.txt_save);
        txt_remove = findViewById(R.id.txt_remove);
        img_apply = findViewById(R.id.img_apply);
        noti_price = findViewById(R.id.noti_price);
        email_price = findViewById(R.id.email_price);
        sms_price = findViewById(R.id.sms_price);
        total_price = findViewById(R.id.total_price);
        discount_price = findViewById(R.id.discount_price);
        final_price = findViewById(R.id.final_price);
        ll_save = findViewById(R.id.ll_save);
        txt_coupon_discout = findViewById(R.id.txt_coupon_discout);
        rl_discount = findViewById(R.id.rl_discount);
        text_noti = findViewById(R.id.text_noti);

        Intent intent_date = getIntent();
        String save = intent_date.getStringExtra("dis_30");
        String selecte_plan = getSharedPreferences("myPlan", MODE_PRIVATE).getString("star_promotion", null);

        if (selecte_plan.equalsIgnoreCase("Existing Users")) {
            text_noti.setText("Free");
            noti_price.setText("0");
        } else if (selecte_plan.equalsIgnoreCase("App Users")) {
            text_noti.setText("1.06");
            noti_price.setText("1.06");
        }

        df = new DecimalFormat("#.###");
        n1 = Double.parseDouble(email_price.getText().toString());
        n2 = Double.parseDouble(sms_price.getText().toString());
        n3 = Double.parseDouble(noti_price.getText().toString());
        sum = n1 + n2 + n3;
        total_price.setText(df.format(sum));

        String publish_date = getSharedPreferences("Datetime", MODE_PRIVATE).getString("date_time", null);
        txt_date_time.setText(publish_date);

        if (save != null) {
            ll_save.setVisibility(View.VISIBLE);
            txt_remove.setVisibility(View.VISIBLE);
            img_apply.setVisibility(View.GONE);
            txt_save.setText(save);
            rl_discount.setVisibility(View.VISIBLE);
            txt_coupon_discout.setText(save);

            dis = (sum * Integer.parseInt(save)) / 100;
            discount_price.setText(String.valueOf(dis));

            ftotal = sum - dis;
            final_price.setText(df.format(ftotal));
        }

        if (save != null) {
            txt_save.setVisibility(View.VISIBLE);
            txt_remove.setVisibility(View.VISIBLE);
            img_apply.setVisibility(View.GONE);
            txt_save.setText(save);
        }

        txt_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_apply.setVisibility(View.VISIBLE);
                ll_save.setVisibility(View.GONE);
                txt_remove.setVisibility(View.GONE);
                rl_discount.setVisibility(View.GONE);
            }
        });

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        img_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PromotionBillActivity.this, ApplyCouponActivity.class));
                finish();
            }
        });

        btn_pay_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PromotionBillActivity.this, PaymentActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PromotionBillActivity.this, PublishDateTimeActivity.class));
        finish();
    }
}