package com.festum.festumfield.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.festum.festumfield.Activity.ChatProductSelectActivity;
import com.festum.festumfield.Activity.ProductDetailsActivity;
import com.festum.festumfield.Model.Product.ProductDetailsModel;
import com.festum.festumfield.R;
import com.festum.festumfield.Utils.Constans;

import java.util.ArrayList;

public class ChatProductListAdapter extends RecyclerView.Adapter<ChatProductListAdapter.ViewHolder> {

    Activity activity;
    ArrayList<ProductDetailsModel> productDetailsModelArrayList;
    LayoutInflater inflater;

    public ChatProductListAdapter(ChatProductSelectActivity chatProductSelectActivity, ArrayList<ProductDetailsModel> productDetailsModelArrayList) {
        this.activity = chatProductSelectActivity;
        this.productDetailsModelArrayList = productDetailsModelArrayList;
        inflater = LayoutInflater.from(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_all_product_display, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.txt_title_name.setText(productDetailsModelArrayList.get(position).getName());
        holder.txt_des.setText(productDetailsModelArrayList.get(position).getDescription());
        holder.txt_price.setText("$" + String.format(String.valueOf(productDetailsModelArrayList.get(position).getPrice())) + ".00");

        Glide.with(activity).load(Constans.Display_Image_URL + productDetailsModelArrayList.get(position).getImages().get(0)).placeholder(R.drawable.ic_user_img).into(holder.iv_image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ProductDetailsActivity.class);
                intent.putExtra("product", "ChatProduct");
                intent.putExtra("ProductDetailsId", productDetailsModelArrayList.get(position).getId());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productDetailsModelArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_image;
        TextView txt_title_name, txt_des, txt_price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_image = itemView.findViewById(R.id.iv_image);
            txt_title_name = itemView.findViewById(R.id.txt_title_name);
            txt_des = itemView.findViewById(R.id.txt_des);
            txt_price = itemView.findViewById(R.id.txt_price);
        }
    }
}
