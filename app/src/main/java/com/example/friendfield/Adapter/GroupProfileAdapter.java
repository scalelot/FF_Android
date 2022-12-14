package com.example.friendfield.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
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
import androidx.collection.ArraySet;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendfield.Model.CreateGroupModel;
import com.example.friendfield.Model.CreateMessage.User;
import com.example.friendfield.Model.GroupUserModel;
import com.example.friendfield.Activity.GroupProfileDetalisActivity;
import com.example.friendfield.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupProfileAdapter extends RecyclerView.Adapter<GroupProfileAdapter.MyViewHolder> {

    Activity activity;
    List<GroupUserModel> elements;
    List<GroupUserModel> modelList;
    LayoutInflater inflater;
    LinearLayout ll_message, ll_profile, ll_remove;
    TextView txt_message;

    public GroupProfileAdapter(GroupProfileDetalisActivity groupProfileDetalisActivity, List<GroupUserModel> subList) {
        this.activity = groupProfileDetalisActivity;
        this.elements = subList;
        this.modelList = subList;
        inflater = LayoutInflater.from(activity);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_group_profile, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.txt_group_username.setText(elements.get(position).getName());
        holder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(activity);
                View view = LayoutInflater.from(activity).inflate(R.layout.group_details_dialog, null);
                dialog.setContentView(view);

                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                ColorDrawable colorDrawable = new ColorDrawable(Color.TRANSPARENT);
                InsetDrawable insetDrawable = new InsetDrawable(colorDrawable, 80);
                dialog.getWindow().setBackgroundDrawable(insetDrawable);
                dialog.setCanceledOnTouchOutside(true);

                ll_message = dialog.findViewById(R.id.ll_message);
                ll_profile = dialog.findViewById(R.id.ll_profile);
                ll_remove = dialog.findViewById(R.id.ll_remove);
                txt_message = dialog.findViewById(R.id.txt_message);
                ImageView btn_close = dialog.findViewById(R.id.btn_close);

                txt_message.setText("Message " + elements.get(position).getName());

                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public void filterList(ArrayList<GroupUserModel> filterllist) {
        elements = filterllist;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView group_img;
        TextView txt_group_username, txt_phone_number;
        LinearLayout ll_admin;
        RelativeLayout rl_item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            group_img = itemView.findViewById(R.id.group_img);
            txt_group_username = itemView.findViewById(R.id.txt_group_username);
            txt_phone_number = itemView.findViewById(R.id.txt_phone_number);
            ll_admin = itemView.findViewById(R.id.ll_admin);
            rl_item = itemView.findViewById(R.id.rl_item);
        }
    }

//    public void getfilter(String charText) {
//        charText = charText.toLowerCase(Locale.getDefault());
//        elements.clear();
//        if (charText.length() == 0) {
//            elements.addAll(modelList);
//        } else {
//            for (GroupUserModel groupUserModel : modelList) {
////                if (wp.getAnimalName().toLowerCase(Locale.getDefault()).contains(charText)) {
//                if (groupUserModel.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
//                    elements.add(groupUserModel);
//                }
//            }
//        }
//        notifyDataSetChanged();
//    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        modelList.clear();
        if (charText.length() == 0) {
            modelList.addAll(elements);
        } else {
            for (GroupUserModel wp : elements) {
//                if (wp.getAnimalName().toLowerCase(Locale.getDefault()).contains(charText)) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    modelList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

//    @Override
//    public Filter getFilter() {
//
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence charSequence) {
//
//                String searchString = charSequence.toString();
//
//                if (searchString.isEmpty()) {
//
//                    elements = modelList;
//
//                } else {
//
//                    ArrayList<GroupUserModel> tempFilteredList = new ArrayList<>();
//
//                    for (GroupUserModel user : modelList) {
//
//                        // search for user title
//                        if (user.getName().toLowerCase().contains(searchString)) {
//                            tempFilteredList.add(user);
//                        }
//                    }
//
//                    elements = tempFilteredList;
//                }
//
//                FilterResults filterResults = new FilterResults();
//                filterResults.values = elements;
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//                elements = (List<GroupUserModel>) filterResults.values;
//                notifyDataSetChanged();
//            }
//        };
//    }

}
