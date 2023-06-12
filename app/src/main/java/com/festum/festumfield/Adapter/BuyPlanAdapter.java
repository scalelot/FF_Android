package com.festum.festumfield.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.festum.festumfield.Activity.SelectUserPackageActivity;
import com.festum.festumfield.R;

public class BuyPlanAdapter extends RecyclerView.Adapter<BuyPlanAdapter.ViewHolder> {

    SelectUserPackageActivity activity;
    String[] plan_name;
    String[] plan_notification;
    String[] plan_sms;
    String[] plan_email;
    String[] plan_all;
    public static int selectedPosition = -1;
    private static ClickListener clickListener;

    public interface ClickListener {
        void onItemClick(String plan_name, boolean isToAdd);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        BuyPlanAdapter.clickListener = clickListener;
    }

    public BuyPlanAdapter(SelectUserPackageActivity selectUserPackageActivity, String[] plan_name, String[] plan_notification, String[] plan_sms, String[] plan_email, String[] plan_all) {
        this.activity = selectUserPackageActivity;
        this.plan_name = plan_name;
        this.plan_notification = plan_notification;
        this.plan_sms = plan_sms;
        this.plan_email = plan_email;
        this.plan_all = plan_all;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buy_plan_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (plan_name[position] == ("FOR 999 USERS")) {
            holder.rl_plan.setBackgroundResource(R.drawable.rectangle_basic);

        } else if (plan_name[position] == ("FOR 9999 USERS")) {
            holder.rl_plan.setBackgroundResource(R.drawable.rectangle_blue);

        } else if (plan_name[position] == ("FOR 99999 USERS")) {
            holder.rl_plan.setBackgroundResource(R.drawable.rectangle_premium);

        }

        holder.chk_select_user.setText(plan_name[position]);
        holder.txt_noti.setText(plan_notification[position]);
        holder.txt_sms.setText(plan_sms[position]);
        holder.txt_email.setText(plan_email[position]);
        holder.txt_all.setText(plan_all[position]);

        holder.chk_select_user.setChecked(position == selectedPosition);

        holder.chk_select_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == selectedPosition) {
                    holder.chk_select_user.setChecked(false);
                    selectedPosition = -1;
                    clickListener.onItemClick("", false);
                } else {
                    selectedPosition = position;
                    notifyDataSetChanged();
                    clickListener.onItemClick(plan_name[position], true);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return plan_name.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_noti, txt_sms, txt_email, txt_all;
        CheckBox chk_select_user;
        RelativeLayout rl_plan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_noti = itemView.findViewById(R.id.txt_noti);
            txt_all = itemView.findViewById(R.id.txt_all);
            chk_select_user = itemView.findViewById(R.id.chk_select_user);
            txt_sms = itemView.findViewById(R.id.txt_sms);
            txt_email = itemView.findViewById(R.id.txt_email);
            rl_plan = itemView.findViewById(R.id.rl_plan);
        }
    }
}
