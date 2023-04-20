package com.example.friendfield.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendfield.Model.CreateGroupModel;
import com.example.friendfield.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserSelectionfinalAdapter extends RecyclerView.Adapter<UserSelectionfinalAdapter.MyDataViewHolder> {

    Context activity;
    ArrayList<CreateGroupModel> user_name;
    LayoutInflater inflater;
    ArrayList<CreateGroupModel> arraylist;

    private static RemoveClickListener removeClickListener;

    public interface RemoveClickListener {
        void onItemClick(CreateGroupModel createGroupModel, int position,View v);
    }

    public void setOnItemClickListener(RemoveClickListener clickListener) {
        UserSelectionfinalAdapter.removeClickListener = clickListener;
    }

    public UserSelectionfinalAdapter(Context context, ArrayList<CreateGroupModel> user_name) {
        this.activity = context;
        this.user_name = user_name;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(this.user_name);
        inflater = LayoutInflater.from(activity);
    }

    @NonNull
    @Override
    public MyDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.selected_contact_list_final, parent, false);
        return new MyDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyDataViewHolder holder, int position) {
//        holder.img_user.setImageResource(user_img[position]);
        holder.txt_user_name.setText(arraylist.get(position).getUsername());

        holder.iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                removeClickListener.onItemClick(arraylist.get(position),position, view);
            }
        });
    }

    @Override
    public int getItemCount() {
        return user_name.size();
    }

    class MyDataViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img_user;
        TextView txt_user_name;
        RelativeLayout ll_user_view;
        ImageView iv_close;

        public MyDataViewHolder(@NonNull View itemView) {
            super(itemView);

            img_user = itemView.findViewById(R.id.img_user);
            txt_user_name = itemView.findViewById(R.id.txt_user_name);
            ll_user_view = itemView.findViewById(R.id.ll_user_view);
            iv_close = itemView.findViewById(R.id.iv_close);

        }
    }
}
