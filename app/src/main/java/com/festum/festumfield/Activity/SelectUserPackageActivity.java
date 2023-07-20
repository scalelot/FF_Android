package com.festum.festumfield.Activity;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.festum.festumfield.Adapter.BuyPlanAdapter;
import com.festum.festumfield.BaseActivity;
import com.festum.festumfield.R;

public class SelectUserPackageActivity extends BaseActivity {

    ImageView ic_back;
    RecyclerView recycler_buy_plan;
    AppCompatButton btn_continue;
    BuyPlanAdapter buyPlanAdapter;
    String[] plan_name = {"FOR 999 USERS", "FOR 9999 USERS", "FOR 99999 USERS"};
    String[] plan_notification = {"69 FOR 999 USERS", "299 FOR 9999 USERS", "1799 FOR 99999 USERS"};
    String[] plan_sms = {"399 FOR 999 USERS", "1999 FOR 9999 USERS", "16999 FOR 99999 USERS"};
    String[] plan_email = {"89 FOR 999 USERS", "8399 FOR 9999 USERS", "3399 FOR 99999 USERS"};
    String[] plan_all = {"475 FOR 999 USERS", "2299 FOR 9999 USERS", "18999 FOR 99999 USERS"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecte_user_package);

        ic_back = findViewById(R.id.ic_back);
        recycler_buy_plan = findViewById(R.id.recycler_buy_plan);
        btn_continue = findViewById(R.id.btn_continue);

        recycler_buy_plan.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        buyPlanAdapter = new BuyPlanAdapter(this, plan_name, plan_notification, plan_sms, plan_email, plan_all);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recycler_buy_plan.setLayoutManager(manager);
        recycler_buy_plan.setAdapter(buyPlanAdapter);

        buyPlanAdapter.setOnItemClickListener(new BuyPlanAdapter.ClickListener() {
            @Override
            public void onItemClick(String plan_name, boolean isToAdd) {
                btn_continue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isToAdd == true) {
                            System.out.println("count " + plan_name);
                            Intent intent = new Intent(SelectUserPackageActivity.this, ChooseUserPromotionActivity.class);
                            intent.putExtra("package_name", plan_name);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(SelectUserPackageActivity.this, "Plaese Select Package", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SelectUserPackageActivity.this, ChooseUserPromotionActivity.class));
        finish();
    }

}