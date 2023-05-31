package com.festum.festumfield.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.festum.festumfield.R;
import com.festum.festumfield.Utils.Constans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class MessageInfoActivity extends AppCompatActivity {

    ImageView ic_back;
    String name, des, image, price, message;
    ImageView pro_chat_image;
    TextView txt_pro_name, txt_time;
    TextView txt_pro_des;
    TextView txt_pro_price, deliver_time;
    TextView txt_product;
    String delivered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_info);

        name = getIntent().getStringExtra("Name");
        des = getIntent().getStringExtra("Des");
        price = getIntent().getStringExtra("Price");
        image = getIntent().getStringExtra("Image");
        message = getIntent().getStringExtra("Message");
        delivered = getIntent().getStringExtra("Delivered");

        ic_back = findViewById(R.id.ic_back);
        pro_chat_image = findViewById(R.id.pro_chat_image);
        txt_pro_name = findViewById(R.id.txt_pro_name);
        txt_pro_des = findViewById(R.id.txt_pro_des);
        txt_pro_price = findViewById(R.id.txt_pro_price);
        txt_product = findViewById(R.id.txt_product);
        deliver_time = findViewById(R.id.deliver_time);
        txt_time = findViewById(R.id.txt_time);

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Glide.with(MessageInfoActivity.this).load(Constans.Display_Image_URL + image).placeholder(R.drawable.ic_user_img).into(pro_chat_image);
        txt_pro_name.setText(name);
        txt_pro_des.setText(des);
        txt_pro_price.setText("$" + price + ".00");
        txt_product.setText(message);

        deliver_time.setText(getDate(delivered));
        txt_time.setText(getDate(delivered));
    }

    private String getDate(String timeStamp) {
        DateTimeFormatter sourceFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        DateTimeFormatter targetFormat = DateTimeFormatter.ofPattern("HH:mm a");
        LocalDateTime dateTime = LocalDateTime.parse(timeStamp, sourceFormat);
        String formatedDateTime = dateTime.atZone(ZoneId.of("UTC")).format(targetFormat);
        return formatedDateTime;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MessageInfoActivity.this, ChatingActivity.class));
        finish();
    }
}