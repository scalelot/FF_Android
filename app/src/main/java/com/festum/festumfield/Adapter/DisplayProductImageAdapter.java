package com.festum.festumfield.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.festum.festumfield.Model.ImageSliderModel;
import com.example.friendfield.R;
import com.festum.festumfield.Utils.Constans;

import java.util.List;

public class DisplayProductImageAdapter extends PagerAdapter {
    Context mContext;
    List<ImageSliderModel> imageSliderModelList;

    public DisplayProductImageAdapter(Context mContext, List<ImageSliderModel> imageSliderModelList) {
        this.mContext = mContext;
        this.imageSliderModelList = imageSliderModelList;
    }

    @Override
    public int getCount() {
        return imageSliderModelList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.display_product_image_item, container, false);

        ImageView imageView = itemView.findViewById(R.id.all_image);
        Glide.with(mContext).load(Constans.Display_Image_URL + imageSliderModelList.get(position).getImage()).into(imageView);
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

}
