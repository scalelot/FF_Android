package com.example.friendfield.Adapter;

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
import com.example.friendfield.Activity.DisplayAllProductActivity;
import com.example.friendfield.Activity.ProductDetailsActivity;
import com.example.friendfield.Model.AllMyFriends.AllFriendsRegisterModel;
import com.example.friendfield.Model.Product.ProductDetailsModel;
import com.example.friendfield.R;
import com.example.friendfield.Utils.Constans;

import java.util.ArrayList;

public class DisplayAllProductAdapter extends RecyclerView.Adapter<DisplayAllProductAdapter.MyDataHolder> {

    Activity activity;
    ArrayList<ProductDetailsModel> arrayList;
    LayoutInflater inflater;

    public DisplayAllProductAdapter(DisplayAllProductActivity displayAllProductActivity, ArrayList<ProductDetailsModel> productDetailsModelArrayList) {
        this.activity = displayAllProductActivity;
        this.arrayList = productDetailsModelArrayList;
        inflater = LayoutInflater.from(activity);
    }

    @NonNull
    @Override
    public MyDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_all_product_display, parent, false);
        return new MyDataHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyDataHolder holder, int position) {
        holder.txt_title_name.setText(arrayList.get(position).getName());
        holder.txt_des.setText(arrayList.get(position).getDescription());
        holder.txt_price.setText("$" + String.format(String.valueOf(arrayList.get(position).getPrice())) + ".00");

        Glide.with(activity).load(Constans.Display_Image_URL + arrayList.get(position).getImages().get(0)).placeholder(R.drawable.ic_user_img).into(holder.iv_image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(activity, ProductDetailsActivity.class);
//                intent.putExtra("product", "AllProduct");
//                intent.putExtra("ProductDetailsId", arrayList.get(position).getId());
//                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class MyDataHolder extends RecyclerView.ViewHolder {

        TextView txt_title_name, txt_des, txt_price;
        ImageView iv_image;

        public MyDataHolder(@NonNull View itemView) {
            super(itemView);

            iv_image = itemView.findViewById(R.id.iv_image);
            txt_title_name = itemView.findViewById(R.id.txt_title_name);
            txt_des = itemView.findViewById(R.id.txt_des);
            txt_price = itemView.findViewById(R.id.txt_price);
        }
    }

    public void filterList(ArrayList<ProductDetailsModel> filterlist) {
        arrayList = filterlist;
        notifyDataSetChanged();
    }
}
