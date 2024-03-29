package com.festum.festumfield.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.festum.festumfield.BaseActivity;
import com.festum.festumfield.R;

public class ConversationActivity extends BaseActivity {

    ImageView ic_back;
    EditText edt_chating;
    RelativeLayout btn_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        ic_back = findViewById(R.id.ic_back);
        edt_chating = findViewById(R.id.edt_conversation);
        btn_send = findViewById(R.id.btn_send);

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ConversationActivity.this, SettingActivity.class));
        finish();
    }
}