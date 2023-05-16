package com.festum.festumfield.Activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.festum.festumfield.Adapter.DetailsDialogAdapter;
import com.festum.festumfield.BaseActivity;
import com.example.friendfield.R;

public class PromotionDetailsActivity extends BaseActivity {

    ImageView ic_back, product_image;
    TextView txt_noti_people, txt_sms_notification, txt_email_people, txt_people, txt_new_product;
    LinearLayout ll_notifi_people;
    int[] details_img = {R.drawable.ic_user, R.drawable.ic_user, R.drawable.ic_user, R.drawable.ic_user, R.drawable.ic_user, R.drawable.ic_user, R.drawable.ic_user, R.drawable.ic_user, R.drawable.ic_user, R.drawable.ic_user, R.drawable.ic_user, R.drawable.ic_user};
    String[] details_name = {"John Bryan", "John Bryan", "John Bryan", "John Bryan", "John Bryan", "John Bryan", "John Bryan", "John Bryan", "John Bryan", "John Bryan", "John Bryan", "John Bryan", "John Bryan"};
    TextView txt_des;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_details);

        ic_back = findViewById(R.id.ic_back);
        product_image = findViewById(R.id.product_image);
        txt_noti_people = findViewById(R.id.txt_noti_people);
        txt_sms_notification = findViewById(R.id.txt_sms_people);
        txt_email_people = findViewById(R.id.txt_email_people);
        txt_people = findViewById(R.id.txt_people);
        ll_notifi_people = findViewById(R.id.ll_notifi_people);
        txt_new_product = findViewById(R.id.txt_new_product);
        txt_des = findViewById(R.id.txt_des);

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ll_notifi_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                peopleNotifiDialog();
            }
        });

    }

    private void peopleNotifiDialog() {
        Dialog dialog = new Dialog(PromotionDetailsActivity.this);
        View view = LayoutInflater.from(PromotionDetailsActivity.this).inflate(R.layout.people_noti_dialog, null);
        dialog.setContentView(view);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable insetDrawable = new InsetDrawable(back, 50);
        dialog.getWindow().setBackgroundDrawable(insetDrawable);
        dialog.setCanceledOnTouchOutside(true);
        ImageView dialog_close = view.findViewById(R.id.dialog_close);

        dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.dialog_recycler);
        DetailsDialogAdapter dialogAdapter = new DetailsDialogAdapter(this, details_img, details_name);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(dialogAdapter);

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}