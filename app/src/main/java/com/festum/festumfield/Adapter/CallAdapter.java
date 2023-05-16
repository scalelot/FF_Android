package com.festum.festumfield.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.festum.festumfield.Fragment.CallsFragment;
import com.festum.festumfield.R;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.MyDataHolder> {

    Fragment fragment;
    int[] call_img;
    LayoutInflater inflater;
    ArrayList<String> user_name;
    ArrayList<String> arraylist;

    public CallAdapter(CallsFragment fragment, ArrayList<String> user_name, int[] call_img) {
        this.fragment = fragment;
        this.user_name = user_name;
        this.call_img = call_img;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(this.user_name);
        inflater = LayoutInflater.from(fragment.getActivity());
    }

    @NonNull
    @Override
    public MyDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_call_history, parent, false);
        return new MyDataHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyDataHolder holder, int position) {
        holder.call_user_name.setText(user_name.get(position));
        holder.call_user_profile.setImageResource(call_img[position]);
    }

    @Override
    public int getItemCount() {
        return user_name.size();
    }

    class MyDataHolder extends RecyclerView.ViewHolder {

        TextView call_user_name, txt_call_time;
        CircleImageView call_user_profile;

        public MyDataHolder(@NonNull View itemView) {
            super(itemView);
            call_user_name = itemView.findViewById(R.id.call_user_name);
            txt_call_time = itemView.findViewById(R.id.txt_call_time);
            call_user_profile = itemView.findViewById(R.id.call_user_profile);
        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        user_name.clear();
        if (charText.length() == 0) {
            user_name.addAll(arraylist);
        } else {
            for (String wp : arraylist) {
                if (wp.toLowerCase(Locale.getDefault()).contains(charText)) {
                    user_name.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}
