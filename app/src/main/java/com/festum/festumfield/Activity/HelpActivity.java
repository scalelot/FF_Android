package com.festum.festumfield.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.festum.festumfield.Adapter.HelpAdapter;
import com.festum.festumfield.BaseActivity;
import com.festum.festumfield.Model.HelpModel;
import com.example.friendfield.R;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends BaseActivity {

    ImageView hp_back_arrow;
    RecyclerView help_recycler;
    HelpAdapter helpAdapter;
    String[] name = {"FAQ-1", "FAQ-2", "FAQ-3", "FAQ-4", "FAQ-5"};
    String[] description = {"It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout.","It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout.","It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout.","It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout.","It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout."};
    List<HelpModel> helpModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        hp_back_arrow = findViewById(R.id.hp_back_arrow);
        help_recycler = findViewById(R.id.help_recycler);

        hp_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        helpModels = new ArrayList<>();

        for (int i = 0; i < name.length; i++) {
            HelpModel hModel = new HelpModel();
            hModel.setName(name[i]);
            hModel.setDescription(description[i]);
            helpModels.add(hModel);
        }

        helpAdapter = new HelpAdapter(this, helpModels);
        help_recycler.setAdapter(helpAdapter);
        help_recycler.setHasFixedSize(true);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(HelpActivity.this, SettingActivity.class));
        finish();
    }
}