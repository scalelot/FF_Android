package com.example.friendfield.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendfield.Fragment.ContactFragment;
import com.example.friendfield.Model.CreateGroupModel;
import com.example.friendfield.R;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllContactAdapter extends RecyclerView.Adapter<AllContactAdapter.MyDataViewHolder> {

    Context activity;
    ArrayList<CreateGroupModel> user_name;
    LayoutInflater inflater;
    ArrayList<CreateGroupModel> arraylist;
    private static ClickListener clickListener;
    public static String isSelectedname;

    public interface ClickListener {
        //        void onItemClick(int position, View v);
//        void onItemClick(String username, ImageView v, boolean isToAdd);
        void onItemClick(CreateGroupModel username, ImageView v, boolean isToAdd);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        AllContactAdapter.clickListener = clickListener;
    }

    public AllContactAdapter(Context context, ArrayList<CreateGroupModel> user_name) {
        this.activity = context;
        this.arraylist = user_name;
        inflater = LayoutInflater.from(activity);
    }

    @NonNull
    @Override
    public MyDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.all_contact_list, parent, false);
        return new MyDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyDataViewHolder holder, int position) {
//        holder.img_user.setImageResource(user_img[position]);
        holder.txt_user_name.setText(arraylist.get(position).getUsername());
        holder.txt_message.setText(arraylist.get(position).getUsername());

        if (arraylist.get(position).getSelected()) {
            holder.iv_selected.setVisibility(View.VISIBLE);
        } else {
            holder.iv_selected.setVisibility(View.GONE);
        }

        holder.ll_user_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (arraylist.get(position).getSelected()) {
                    arraylist.get(position).setSelected(false);
                    holder.iv_selected.setVisibility(View.GONE);
                    clickListener.onItemClick(arraylist.get(position), holder.iv_selected, false);
                } else {
                    arraylist.get(position).setCheckname(arraylist.get(position).getUsername());
                    arraylist.get(position).setSelected(true);
                    isSelectedname = arraylist.get(position).getCheckname();
                    holder.iv_selected.setVisibility(View.VISIBLE);
                    clickListener.onItemClick(arraylist.get(position), holder.iv_selected, true);
                }

            }
        });
    }

    // method for filtering our recyclerview items.
    public void filterList(ArrayList<CreateGroupModel> filterllist) {
        arraylist = filterllist;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    public static String getIsSelectedname() {
        return isSelectedname;
    }

    class MyDataViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img_user;
        TextView txt_user_name, txt_message;
        LinearLayout ll_user_view;
        ImageView iv_selected;

        public MyDataViewHolder(@NonNull View itemView) {
            super(itemView);

            img_user = itemView.findViewById(R.id.img_user);
            txt_user_name = itemView.findViewById(R.id.txt_user_name);
            txt_message = itemView.findViewById(R.id.txt_message);
            ll_user_view = itemView.findViewById(R.id.ll_user_view);
            iv_selected = itemView.findViewById(R.id.iv_selected);

        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        user_name.clear();
        if (charText.length() == 0) {
            user_name.addAll(arraylist);
        } else {
            for (CreateGroupModel wp : arraylist) {
//                if (wp.getAnimalName().toLowerCase(Locale.getDefault()).contains(charText)) {
                if (wp.getUsername().toLowerCase(Locale.getDefault()).contains(charText)) {
                    user_name.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
