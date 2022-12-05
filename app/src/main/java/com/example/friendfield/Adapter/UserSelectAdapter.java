package com.example.friendfield.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendfield.Activity.SelectUserActivity;
import com.example.friendfield.Model.SelecetdUserModel;
import com.example.friendfield.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserSelectAdapter extends RecyclerView.Adapter<UserSelectAdapter.MyViewHolder> {

    Activity activity;
    int[] u_img;
    String[] u_name;
    LayoutInflater inflater;
    ArrayList<SelecetdUserModel> selecetdUserModelArrayList;

    public UserSelectAdapter(SelectUserActivity selectUserActivity, int[] user_img, String[] user_name) {
        this.activity = selectUserActivity;
        this.u_img = user_img;
        this.u_name = user_name;
        inflater = LayoutInflater.from(activity);
    }

    public UserSelectAdapter(Activity activity, ArrayList<SelecetdUserModel> selecetdUserModelArrayList) {
        this.activity = activity;
        this.selecetdUserModelArrayList = selecetdUserModelArrayList;
        inflater = LayoutInflater.from(activity);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_user_select, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txt_username.setText(selecetdUserModelArrayList.get(position).getUsername());
        holder.txt_gmail.setText(selecetdUserModelArrayList.get(position).getUsername());
//        holder.profile_img.setImageResource(u_img[position]);
        holder.img_chk.setChecked(selecetdUserModelArrayList.get(position).getSelected() ? true : false);

        holder.img_chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                }
            }
        });

    }

    public void selectAllItem(boolean isSelectedAll) {
        try {
            if (selecetdUserModelArrayList != null) {
                for (int index = 0; index < selecetdUserModelArrayList.size(); index++) {
                    selecetdUserModelArrayList.get(index).setSelected(isSelectedAll);
                }
            }
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return selecetdUserModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile_img;
        TextView txt_username, txt_gmail;
        RelativeLayout iv_circle_image;
        ImageView iv_type_img;
        CheckBox img_chk;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_img = itemView.findViewById(R.id.profile_img);
            txt_username = itemView.findViewById(R.id.txt_username);
            txt_gmail = itemView.findViewById(R.id.txt_gmail);

            iv_circle_image = itemView.findViewById(R.id.iv_circle_image);
            iv_type_img = itemView.findViewById(R.id.iv_type_img);
            img_chk = itemView.findViewById(R.id.img_chk);


        }
    }
}
