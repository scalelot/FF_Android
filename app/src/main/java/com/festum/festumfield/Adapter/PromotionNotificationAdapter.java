package com.festum.festumfield.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.festum.festumfield.Activity.CreateNotificationActivity;
import com.festum.festumfield.Activity.PromotionDetailsActivity;
import com.festum.festumfield.Activity.PromotionPlanActivity;
import com.festum.festumfield.Model.Notification.NotificationDetailsModel;
import com.festum.festumfield.R;
import com.festum.festumfield.Utils.Constans;

import java.util.ArrayList;

public class PromotionNotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Activity activity;
    ArrayList<NotificationDetailsModel> listNotificationModelArrayList;
    private static final int CONTENT_TYPE = 0;
    private static final int AD_TYPE = 1;

    public PromotionNotificationAdapter(Activity activity, ArrayList<NotificationDetailsModel> listNotificationModelArrayList) {
        this.activity = activity;
        this.listNotificationModelArrayList = listNotificationModelArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == AD_TYPE) {
            View view1 = LayoutInflater.from(activity).inflate(R.layout.list_ads_view, parent, false);
            return new AdRecyclerHolder(view1);
        } else {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_noti_promotion, parent, false);
            return new MyViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (listNotificationModelArrayList.get(position) == null)
            return AD_TYPE;
        return CONTENT_TYPE;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        int viewType = getItemViewType(position);

        if (viewType == CONTENT_TYPE) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            Glide.with(activity).load(Constans.Display_Image_URL + listNotificationModelArrayList.get(position).getImageUrl()).into(((MyViewHolder) holder).notification_img);

            myViewHolder.notification_txt_name.setText(listNotificationModelArrayList.get(position).getTitle());
            myViewHolder.notification_des.setText(listNotificationModelArrayList.get(position).getDescription());
            myViewHolder.notification_link.setText(listNotificationModelArrayList.get(position).getLink());
            myViewHolder.ll_promotion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.getContext().startActivity(new Intent(activity, PromotionPlanActivity.class));
                    activity.finish();
                }
            });

            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent img_intent = new Intent(activity, PromotionDetailsActivity.class);
                    img_intent.putExtra("notificationList", listNotificationModelArrayList.get(position));
                    activity.startActivity(img_intent);
                }
            });

            myViewHolder.btn_edit_promotion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(new Intent(activity, CreateNotificationActivity.class).putExtra("Edit_Noti", activity.getResources().getString(R.string.edit_product)).putExtra("noti_id", listNotificationModelArrayList.get(position).getId()));
                    activity.finish();

                }
            });
        } else {
            AdRecyclerHolder adRecyclerHolder = (AdRecyclerHolder) holder;

        }
    }

    @Override
    public int getItemCount() {
        return listNotificationModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView notification_txt_name, notification_des, notification_link;
        ImageView notification_img;
        AppCompatButton btn_edit_promotion;
        LinearLayout ll_promotion;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ll_promotion = itemView.findViewById(R.id.ll_promotion);
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
