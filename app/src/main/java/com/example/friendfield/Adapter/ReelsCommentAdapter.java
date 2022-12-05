package com.example.friendfield.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendfield.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReelsCommentAdapter extends RecyclerView.Adapter<ReelsCommentAdapter.MyViewHolder> {

    Context activity;
    String[] name;

    public ReelsCommentAdapter(Context reelsActivity, String[] name) {
        this.activity = reelsActivity;
        this.name = name;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_user, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.userName.setText(name[position]);
        holder.txt_usergmail.setText(name[position]);
    }

    @Override
    public int getItemCount() {
        return name.length;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView img_user;
        TextView userName, txt_usergmail;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            img_user = itemView.findViewById(R.id.img_user);
            userName = itemView.findViewById(R.id.userName);
            txt_usergmail = itemView.findViewById(R.id.txt_usergmail);
        }
    }
}
