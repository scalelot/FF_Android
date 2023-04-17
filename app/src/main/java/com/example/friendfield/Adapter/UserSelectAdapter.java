package com.example.friendfield.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendfield.Model.SelecetdUserModel;
import com.example.friendfield.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserSelectAdapter extends RecyclerView.Adapter<UserSelectAdapter.MyViewHolder> {

    Activity activity;
    LayoutInflater inflater;
    ArrayList<SelecetdUserModel> selecetdUserModelArrayList;
    static ClickListener clickListener;

    public interface ClickListener {
        void onItemClick(SelecetdUserModel model, boolean isAddTo, int position);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        UserSelectAdapter.clickListener = clickListener;
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
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.txt_username.setText(selecetdUserModelArrayList.get(position).getuserName());
        holder.txt_gmail.setText(selecetdUserModelArrayList.get(position).getuserName());
        
        holder.chkBox.setChecked(selecetdUserModelArrayList.get(position).isSelected);

        holder.chkBox.setTag(selecetdUserModelArrayList.get(position));

        holder.chkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                SelecetdUserModel model = (SelecetdUserModel) cb.getTag();

                if (!selecetdUserModelArrayList.get(position).isSelected) {
                    model.setSelected(cb.isChecked());
                    selecetdUserModelArrayList.get(position).setSelected(cb.isChecked());
                    clickListener.onItemClick(selecetdUserModelArrayList.get(position), true,position);
                    notifyDataSetChanged();
                } else {
                    model.setSelected(!cb.isChecked());
                    selecetdUserModelArrayList.get(position).setSelected(false);
                    clickListener.onItemClick(selecetdUserModelArrayList.get(position), false,position);
                    notifyDataSetChanged();
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
                notifyDataSetChanged();
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
        CheckBox chkBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_img = itemView.findViewById(R.id.profile_img);
            txt_username = itemView.findViewById(R.id.txt_username);
            txt_gmail = itemView.findViewById(R.id.txt_gmail);

            iv_circle_image = itemView.findViewById(R.id.iv_circle_image);
            iv_type_img = itemView.findViewById(R.id.iv_type_img);
            chkBox = itemView.findViewById(R.id.chkBox);


        }
    }
}
