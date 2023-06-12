package com.festum.festumfield.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.festum.festumfield.Activity.PromotionDetailsActivity;
import com.festum.festumfield.R;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Activity activity;
    ArrayList<String> txt_usernae;
    LayoutInflater inflater;
    private static final int CONTENT_TYPE = 0;
    private static final int AD_TYPE = 1;

    public NotificationAdapter(FragmentActivity activity, ArrayList<String> txt_userName) {
        this.activity = activity;
        this.txt_usernae = txt_userName;
        inflater = LayoutInflater.from(activity);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == AD_TYPE) {
            View view1 = inflater.inflate(R.layout.list_ads_view, parent, false);
            return new AdRecyclerHolder(view1);
        } else {
            View view = inflater.inflate(R.layout.item_noti, parent, false);
            return new MyViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (txt_usernae.get(position) == null)
            return AD_TYPE;
        return CONTENT_TYPE;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        int viewType = getItemViewType(position);

        if (viewType == CONTENT_TYPE) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            myViewHolder.notification_txt_name.setText(txt_usernae.get(position));
            myViewHolder.notification_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

            myViewHolder.notification_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent img_intent = new Intent(activity, PromotionDetailsActivity.class);
                    img_intent.putExtra("noti_title", txt_usernae.get(position));
                    activity.startActivity(img_intent);
                }
            });
        } else {
            AdRecyclerHolder adRecyclerHolder = (AdRecyclerHolder) holder;

        }
    }

    @Override
    public int getItemCount() {
        return txt_usernae.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView notification_txt_name, notification_des, notification_link;
        ImageView notification_img;
        AppCompatButton btn_edit_promotion;
        AppCompatButton notification_btn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            notification_btn  = itemView.findViewById(R.id.notification_btn );
            btn_edit_promotion = itemView.findViewById(R.id.btn_edit_promotion);
            notification_link = itemView.findViewById(R.id.notification_link);
            notification_des = itemView.findViewById(R.id.notification_des);
            notification_txt_name = itemView.findViewById(R.id.notification_txt_name);
            notification_img = itemView.findViewById(R.id.notification_img);
        }
    }

    class AdRecyclerHolder extends RecyclerView.ViewHolder {

        public AdRecyclerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
