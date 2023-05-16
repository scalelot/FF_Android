package com.festum.festumfield.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.festum.festumfield.Activity.HelpActivity;
import com.festum.festumfield.Model.HelpModel;
import com.festum.festumfield.R;

import java.util.List;

public class HelpAdapter extends RecyclerView.Adapter<HelpAdapter.ViewHolder> {

    Activity activity;
    List<HelpModel> helpModels;
    LayoutInflater inflater;

    public HelpAdapter(HelpActivity helpActivity, List<HelpModel> helpModels) {
        this.activity = helpActivity;
        this.helpModels = helpModels;
        inflater = LayoutInflater.from(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_help, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txt_faq.setText(helpModels.get(position).getName());
        holder.txt_description.setText(helpModels.get(position).getDescription());

        boolean isExpandable = helpModels.get(position).isExpandable();
        holder.expandable_layout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);
        holder.img_down_arrow.setVisibility(isExpandable ? View.VISIBLE : View.GONE);
        holder.img_up_arrow.setVisibility(isExpandable ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return helpModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_faq, txt_description;
        ImageView img_up_arrow, img_down_arrow;
        RelativeLayout expandable_layout;
        RelativeLayout relative_layout;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_faq = itemView.findViewById(R.id.txt_faq);
            txt_description = itemView.findViewById(R.id.txt_description);
            img_up_arrow = itemView.findViewById(R.id.img_up_arrow);
            img_down_arrow = itemView.findViewById(R.id.img_down_arrow);
            relative_layout = itemView.findViewById(R.id.relative_layout);
            expandable_layout = itemView.findViewById(R.id.expandable_layout);
            view = itemView.findViewById(R.id.view);

            relative_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HelpModel helpModel = helpModels.get(getAdapterPosition());
                    helpModel.setExpandable(!helpModel.isExpandable());
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }
}
