package com.example.friendfield.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.friendfield.Activity.BlockedContactActivity;
import com.example.friendfield.Model.BlockedFriends.BlockedFriendRegisterModel;
import com.example.friendfield.R;
import com.example.friendfield.Utils.Constans;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlockedContactAdapter extends RecyclerView.Adapter<BlockedContactAdapter.MyViewHolder> {

    Activity activity;
    ArrayList<BlockedFriendRegisterModel> blockedFriendRegisterModels;
    LayoutInflater inflater;

    public BlockedContactAdapter(BlockedContactActivity blockedContactActivity, ArrayList<BlockedFriendRegisterModel> blockedFriendRegisterModels) {
        this.activity = blockedContactActivity;
        this.blockedFriendRegisterModels = blockedFriendRegisterModels;
        inflater = LayoutInflater.from(activity);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.blocked_contact_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(activity).load(Constans.Display_Image_URL + blockedFriendRegisterModels.get(position).getProfileimage()).placeholder(R.drawable.ic_user_img).into(holder.circleImageView);

        holder.txt_contact.setText(blockedFriendRegisterModels.get(position).getContactNo());

        holder.btn_clear_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return blockedFriendRegisterModels.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView txt_contact;
        AppCompatButton btn_clear_data;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.contact_user);
            txt_contact = itemView.findViewById(R.id.txt_contact);
            btn_clear_data = itemView.findViewById(R.id.btn_clear_data);
        }
    }
}
