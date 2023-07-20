package com.festum.festumfield.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.festum.festumfield.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class LikeAndCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Activity activity;
    ArrayList<String> user_name;
    LayoutInflater inflater;
    ArrayList<String> arraylist;
    private static final int CONTENT_TYPE = 0;
    private static final int AD_TYPE = 1;

    public LikeAndCommentAdapter(Activity activity, ArrayList<String> user_name) {
        this.activity = activity;
        this.user_name = user_name;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(this.user_name);
        inflater = LayoutInflater.from(activity);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == AD_TYPE) {
            View view1 = inflater.inflate(R.layout.list_ads_view, parent, false);
            return new AdRecyclerHolder(view1);
        } else {
            View view = inflater.inflate(R.layout.like_and_comment_list, parent, false);
            return new MyDataViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (arraylist.get(position) == null) return AD_TYPE;
        return CONTENT_TYPE;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        if (viewType == CONTENT_TYPE) {
            MyDataViewHolder myDataViewHolder = (MyDataViewHolder) holder;
            myDataViewHolder.txt_user_name.setText(user_name.get(position));
            myDataViewHolder.txt_message.setText(user_name.get(position));

        } else {
            AdRecyclerHolder adRecyclerHolder = (AdRecyclerHolder) holder;

        }
    }

    @Override
    public int getItemCount() {
        return user_name.size();
    }

    class MyDataViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img_user;
        TextView txt_user_name, txt_message, txt_time;
        LinearLayout ll_user_view;
        ImageView iv_user_item;

        public MyDataViewHolder(@NonNull View itemView) {
            super(itemView);

            img_user = itemView.findViewById(R.id.img_user);
            txt_user_name = itemView.findViewById(R.id.txt_user_name);
            txt_message = itemView.findViewById(R.id.txt_message);
            ll_user_view = itemView.findViewById(R.id.ll_user_view);
            iv_user_item = itemView.findViewById(R.id.iv_user_item);
            txt_time = itemView.findViewById(R.id.txt_time);

        }
    }

    class AdRecyclerHolder extends RecyclerView.ViewHolder {

        public AdRecyclerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
