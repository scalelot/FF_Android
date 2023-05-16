package com.festum.festumfield.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.festum.festumfield.Adapter.DisplayProductImageAdapter;
import com.festum.festumfield.Model.ImageSliderModel;
import com.example.friendfield.R;

import java.util.ArrayList;
import java.util.List;

public class DisplayProductImageActivity extends AppCompatActivity {
    ViewPager viewPager;
    ImageView ic_back;
    int pos;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;
    List<ImageSliderModel> imageSliderModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_product_image);

        ic_back = findViewById(R.id.ic_back);
        viewPager = findViewById(R.id.viewPager);
        sliderDotspanel = findViewById(R.id.SliderDots);

        imageSliderModels = (List<ImageSliderModel>) getIntent().getSerializableExtra("listImage");
        pos = getIntent().getIntExtra("pos", 0);

        DisplayProductImageAdapter displayProductImageAdapter = new DisplayProductImageAdapter(getApplicationContext(), imageSliderModels);
        viewPager.setAdapter(displayProductImageAdapter);
        viewPager.setCurrentItem(pos);
        dotscount = displayProductImageAdapter.getCount();
        dots = new ImageView[dotscount];

        for (int i = 0; i < dotscount; i++) {
            dots[i] = new ImageView(getApplicationContext());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            sliderDotspanel.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dotscount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


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