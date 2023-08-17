package com.festum.festumfield.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.festum.festumfield.Activity.DisplayProductImageActivity;
import com.festum.festumfield.Model.ImageSliderModel;
import com.festum.festumfield.R;
import com.festum.festumfield.Utils.Constans;

import java.io.Serializable;
import java.util.List;


public class ImageSliderPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    List<ImageSliderModel> imageSliderModels;

    public ImageSliderPagerAdapter(Context context, List<ImageSliderModel> imageSliderModels) {
        this.imageSliderModels = imageSliderModels;
        this.context = context;
    }

    @Override
    public int getCount() {
        return imageSliderModels.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_layout_viewslider, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        CardView card_image = view.findViewById(R.id.card_image);
        Glide.with(context).load(Constans.Display_Image_URL + imageSliderModels.get(position).getImage()).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, DisplayProductImageActivity.class).putExtra("listImage", (Serializable) imageSliderModels).putExtra("pos", position));
            }
        });


        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }
}
