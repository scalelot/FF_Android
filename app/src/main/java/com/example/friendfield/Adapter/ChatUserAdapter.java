package com.example.friendfield.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.friendfield.Activity.ChatingActivity;
import com.example.friendfield.Fragment.ChatFragment;
import com.example.friendfield.Model.AllMyFriends.AllFriendsRegisterModel;
import com.example.friendfield.Model.CreateMessage.User;
import com.example.friendfield.R;
import com.example.friendfield.Utils.Constans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.MyDataViewHolder>  {

    Fragment fragment;
    ArrayList<AllFriendsRegisterModel> registerModels;
    LayoutInflater inflater;

    public ChatUserAdapter(ChatFragment activity, ArrayList<AllFriendsRegisterModel> receivefriendrequestsModelArrayList) {
        this.fragment = activity;
        this.registerModels = receivefriendrequestsModelArrayList;
        inflater = LayoutInflater.from(fragment.getContext());
    }

    @NonNull
    @Override
    public MyDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.user_chat_list, parent, false);
        return new MyDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyDataViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(fragment).load(Constans.Display_Image_URL + registerModels.get(position).getProfileimage()).placeholder(R.drawable.ic_user_img).into(holder.img_user);

        holder.txt_user_name.setText(registerModels.get(position).getFullName());

        String strTime = String.valueOf(registerModels.get(position).getTimestamp());
        long timestamp = Long.parseLong(strTime) * 1000L;
        if (String.valueOf(timestamp) != null) {
            String time = getDate(timestamp);
            holder.chat_user_time.setText(time);
        }
//        holder.txt_message.setText(registerModels.get(position));


        holder.ll_user_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = fragment.getActivity().getSharedPreferences("ToUserIds", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("ids", registerModels.get(position).getId());
                editor.putString("userName", registerModels.get(position).getFullName());
                editor.putString("images", registerModels.get(position).getProfileimage());
                editor.putString("nickName", registerModels.get(position).getNickName());
                editor.apply();
                editor.commit();

                Intent intent = new Intent(fragment.getContext(), ChatingActivity.class);
                intent.putExtra("UserName", registerModels.get(position).getFullName());
                fragment.startActivity(intent);
            }
        });
    }

    private String getDate(long timeStamp) {
        try {
            DateFormat sdf = new SimpleDateFormat("hh:mm", Locale.US);
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "xx";
        }
    }

    @Override
    public int getItemCount() {
        return registerModels.size();
    }

    public void filterList(ArrayList<AllFriendsRegisterModel> filterlist) {
        registerModels = filterlist;
        notifyDataSetChanged();
    }

    class MyDataViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img_user;
        TextView txt_user_name, chat_user_time, txt_message, txt_chat_count;
        LinearLayout ll_user_view;
        RelativeLayout iv_circle_image;
        ImageView iv_type_img;

        public MyDataViewHolder(@NonNull View itemView) {
            super(itemView);

            img_user = itemView.findViewById(R.id.img_user);
            txt_user_name = itemView.findViewById(R.id.txt_user_name);
            chat_user_time = itemView.findViewById(R.id.chat_user_time);
            txt_message = itemView.findViewById(R.id.txt_message);
            txt_chat_count = itemView.findViewById(R.id.txt_chat_count);
            ll_user_view = itemView.findViewById(R.id.ll_user_view);

            iv_circle_image = itemView.findViewById(R.id.iv_circle_image);
            iv_type_img = itemView.findViewById(R.id.iv_type_img);

        }
    }
}
