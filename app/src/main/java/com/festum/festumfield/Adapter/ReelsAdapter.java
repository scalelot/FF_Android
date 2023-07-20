package com.festum.festumfield.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.festum.festumfield.R;

import java.util.ArrayList;

public class ReelsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Activity activity;
    ArrayList<Integer> img_noti;
    LayoutInflater inflater;
    private static final int CONTENT_TYPE = 0;
    private static final int AD_TYPE = 1;

    public ReelsAdapter(FragmentActivity activity, ArrayList<Integer> img_noti) {
        this.activity = activity;
        this.img_noti = img_noti;
        inflater = LayoutInflater.from(activity);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == AD_TYPE) {
            View view1 = inflater.inflate(R.layout.reels_grid_ads_view, parent, false);
            return new AdRecyclerHolder(view1);
        } else {
            View view = inflater.inflate(R.layout.item_reels, parent, false);
            return new MyDataHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (img_noti.get(position) == null) return AD_TYPE;
        return CONTENT_TYPE;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        if (viewType == CONTENT_TYPE) {
            MyDataHolder myDataHolder = (MyDataHolder) holder;

            myDataHolder.iv_pro_image.setImageResource(img_noti.get(position));
            myDataHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        } else {
            AdRecyclerHolder adRecyclerHolder = (AdRecyclerHolder) holder;

        }
    }

    @Override
    public int getItemCount() {
        return img_noti.size();
    }

    class MyDataHolder extends RecyclerView.ViewHolder {

        ImageView iv_pro_image;
        ImageView iv_play;

        public MyDataHolder(@NonNull View itemView) {
            super(itemView);
            iv_pro_image = itemView.findViewById(R.id.iv_pro_image);
            iv_play = itemView.findViewById(R.id.iv_play);
        }
    }

    class AdRecyclerHolder extends RecyclerView.ViewHolder {

        public AdRecyclerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
