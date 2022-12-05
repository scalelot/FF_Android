package com.example.friendfield.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.friendfield.Adapter.DisplayProductImageAdapter;
import com.example.friendfield.Model.ImageSliderModel;
import com.example.friendfield.R;

import java.util.ArrayList;
import java.util.List;

public class DisplayProductImageActivity extends AppCompatActivity {
    ViewPager viewPager;
    ImageView ic_back;
    int pos;
    List<ImageSliderModel> imageSliderModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_product_image);

        viewPager = findViewById(R.id.viewPager);
        ic_back = findViewById(R.id.ic_back);

        imageSliderModels = (List<ImageSliderModel>) getIntent().getSerializableExtra("listImage");
        pos = getIntent().getIntExtra("pos", 0);

        DisplayProductImageAdapter displayProductImageAdapter = new DisplayProductImageAdapter(getApplicationContext(), imageSliderModels);
        viewPager.setAdapter(displayProductImageAdapter);
        viewPager.setCurrentItem(pos);

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}