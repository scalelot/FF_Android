package com.example.friendfield.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devlomi.circularstatusview.CircularStatusView;
import com.example.friendfield.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecentStatusAdapter extends RecyclerView.Adapter<RecentStatusAdapter.ViewHolder> {
    Context context;
    //    int[][] images;
    String[][] images;
    String[] username;
    String[] user_profile;

    ArrayList<Integer> user_status = new ArrayList<>();
    OnStatusClickListener onStatusClickListener;

    public interface OnStatusClickListener {
        void onStatusClick(CircularStatusView circularStatusView, int pos);
    }

    public void setOnStatusClickListener(OnStatusClickListener onStatusClickListener) {
        this.onStatusClickListener = onStatusClickListener;
    }

    public RecentStatusAdapter(Context context, String[][] images, String[] username, String[] user_profile) {
        this.context = context;
        this.images = images;
        this.username = username;
        this.user_profile = user_profile;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_status, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txt_person_name.setText(username[position]);

        for (int i = 0; i < images[position].length; i++) {
//        for (int i = position; i < images.length; i++) {
            if (!user_status.isEmpty()) {
                user_status.clear();
            }
//            if (position == i) {
            for (int j = 0; j < images[i].length; j++) {
                user_status.add(j);
            }
            Log.e("LLL_stat_-->", i + " " + String.valueOf(user_status.size()));
            holder.circular_status_view.setPortionsCount(user_status.size());
//            }
        }

        Glide.with(context).load(user_profile[position]).placeholder(R.drawable.ic_user).into(holder.person_img);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

////                Intent intent = new Intent(context, OtherStoryViewActivity.class);
//                Intent intent = new Intent(context, AllStoryViewActivity.class);
////                intent.putExtra("Total_Images", images[position]);
//                intent.putExtra("Total_Images", images);
////                intent.putExtra("UserName", username[position]);
//                intent.putExtra("UserName", username);
//                intent.putExtra("UserProfile", user_profile);
//                intent.putExtra("currentPosition", position);
//                context.startActivity(intent);

                if (onStatusClickListener != null)
                    onStatusClickListener.onStatusClick(holder.circular_status_view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView person_img;
        TextView txt_person_name;
        TextView txt_upload_time;
        CircularStatusView circular_status_view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            person_img = itemView.findViewById(R.id.person_img);
            txt_person_name = itemView.findViewById(R.id.txt_person_name);
            txt_upload_time = itemView.findViewById(R.id.txt_upload_time);
            circular_status_view = itemView.findViewById(R.id.circular_status_view);
        }
    }
}
