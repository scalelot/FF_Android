package com.festum.festumfield.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.festum.festumfield.Activity.ChatingActivity;
import com.festum.festumfield.Fragment.ChatFragment;
import com.festum.festumfield.Model.AllMyFriends.AllFriendsRegisterModel;
import com.festum.festumfield.R;
import com.festum.festumfield.Utils.Constans;
import com.festum.festumfield.verstion.firstmodule.screens.main.ChatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.MyDataViewHolder> {

    Fragment fragment;
    ArrayList<AllFriendsRegisterModel> registerModels;

    public ChatUserAdapter(ChatFragment activity, ArrayList<AllFriendsRegisterModel> receivefriendrequestsModelArrayList) {
        this.fragment = activity;
        this.registerModels = receivefriendrequestsModelArrayList;
    }

    @NonNull
    @Override
    public MyDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_chat_list, parent, false);
        return new MyDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyDataViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(fragment).load(Constans.Display_Image_URL + registerModels.get(position).getProfileimage()).placeholder(R.drawable.ic_user_img).into(holder.img_user);

        holder.txt_user_name.setText(registerModels.get(position).getFullName());

        holder.rl_chat_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = fragment.getActivity().getSharedPreferences("ToUserIds", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("ids", registerModels.get(position).getId());
                editor.putString("userName", registerModels.get(position).getFullName());
                editor.putString("images", registerModels.get(position).getProfileimage());
                editor.putString("nickName", registerModels.get(position).getNickName());
                editor.apply();

                /*Intent intent = new Intent(fragment.getContext(), ChatingActivity.class);
                intent.putExtra("UserName", registerModels.get(position).getFullName());
                fragment.startActivity(intent);*/

                Intent intent = new Intent(fragment.getContext(), ChatActivity.class);
                intent.putExtra("userName", registerModels.get(position).getFullName());
                intent.putExtra("id", registerModels.get(position).getId());
                fragment.startActivity(intent);
            }
        });

        String strTime = String.valueOf(registerModels.get(position).getTimestamp());
        long timestamp = Long.parseLong(strTime) * 1000L;
        if (String.valueOf(timestamp) != null) {
            String time = getFormattedDate(timestamp);
            holder.chat_user_time.setText(time);
        }
    }

    @Override
    public int getItemCount() {
        return registerModels.size();
    }

    static class MyDataViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img_user;
        TextView txt_user_name, chat_user_time, txt_message, txt_chat_count;
        RelativeLayout iv_circle_image, rl_chat_layout;
        ImageView iv_type_img;

        public MyDataViewHolder(@NonNull View itemView) {
            super(itemView);

            img_user = itemView.findViewById(R.id.img_user);
            txt_user_name = itemView.findViewById(R.id.txt_user_name);
            chat_user_time = itemView.findViewById(R.id.chat_user_time);
            txt_message = itemView.findViewById(R.id.txt_message);
            txt_chat_count = itemView.findViewById(R.id.txt_chat_count);
            rl_chat_layout = itemView.findViewById(R.id.rl_chat_layout);
            iv_circle_image = itemView.findViewById(R.id.iv_circle_image);
            iv_type_img = itemView.findViewById(R.id.iv_type_img);
        }
    }

    public String getFormattedDate(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", Locale.US);
        Date netDate = (new Date(smsTimeInMilis));
        final String dateTimeFormatString = "EEEE, MMMM d, h:mm aa";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return sdf.format(netDate);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return "Yesterday";
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return android.text.format.DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return android.text.format.DateFormat.format("dd/MM/yy", smsTime).toString();
        }
    }

    public void filterList(ArrayList<AllFriendsRegisterModel> filterlist) {
        registerModels = filterlist;
        notifyDataSetChanged();
    }
}
