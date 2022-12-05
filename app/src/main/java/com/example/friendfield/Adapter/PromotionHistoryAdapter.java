package com.example.friendfield.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendfield.Activity.PromotionDetailsActivity;
import com.example.friendfield.R;

import java.util.ArrayList;

public class PromotionHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Activity activity;
    ArrayList<String> txt_username;
    ArrayList<Integer> img_noti;
    LayoutInflater inflater;
    private static final int CONTENT_TYPE = 0;
    private static final int AD_TYPE = 1;

    public PromotionHistoryAdapter(FragmentActivity activity, ArrayList<String> txt_userName, ArrayList<Integer> img_noti) {
        this.activity = activity;
        this.txt_username = txt_userName;
        this.img_noti = img_noti;
        inflater = LayoutInflater.from(activity);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == AD_TYPE) {
            View view1 = inflater.inflate(R.layout.list_ads_view, parent, false);
            return new AdRecyclerHolder(view1);
        } else {
            View view = inflater.inflate(R.layout.item_noti_history, parent, false);
            return new MyDataHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (txt_username.get(position) == null && img_noti.get(position) == null)
            return AD_TYPE;
        return CONTENT_TYPE;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        if (viewType == CONTENT_TYPE) {
            MyDataHolder myDataHolder = (MyDataHolder) holder;

            myDataHolder.noti_histroy_txt_name.setText(txt_username.get(position));
            myDataHolder.noti_histroy_img.setImageResource(img_noti.get(position));
            myDataHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent img_intent = new Intent(activity, PromotionDetailsActivity.class);
                    img_intent.putExtra("noti_image", img_noti.get(position));
                    img_intent.putExtra("noti_title", txt_username.get(position));
                    activity.startActivity(img_intent);
                }
            });
        }else {
            AdRecyclerHolder adRecyclerHolder = (AdRecyclerHolder) holder;

        }
    }

    @Override
    public int getItemCount() {
        return txt_username.size();
    }

    class MyDataHolder extends RecyclerView.ViewHolder {

        ImageView noti_histroy_img;
        TextView noti_histroy_txt_name;

        public MyDataHolder(@NonNull View itemView) {
            super(itemView);
            noti_histroy_img = itemView.findViewById(R.id.noti_histroy_img);
            noti_histroy_txt_name = itemView.findViewById(R.id.noti_histroy_txt_name);
        }
    }

    class AdRecyclerHolder extends RecyclerView.ViewHolder {

        public AdRecyclerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
