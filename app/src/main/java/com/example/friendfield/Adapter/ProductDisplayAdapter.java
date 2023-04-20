package com.example.friendfield.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.friendfield.Activity.AddProductActivity;
import com.example.friendfield.Activity.ProductDetailsActivity;
import com.example.friendfield.Model.Product.ProductDetailsModel;
import com.example.friendfield.R;
import com.example.friendfield.Utils.Constans;
import com.example.friendfield.Utils.FileUtils;

import java.util.ArrayList;

public class ProductDisplayAdapter extends RecyclerView.Adapter<ProductDisplayAdapter.ViewHolder> {
    Context context;
    ArrayList<ProductDetailsModel> productDetailsModelArrayList;

    public ProductDisplayAdapter(Context context, ArrayList<ProductDetailsModel> productDetailsModelArrayList) {
        this.context = context;
        this.productDetailsModelArrayList = productDetailsModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (productDetailsModelArrayList.get(position).getImages().size() > 0) {
            Glide.with(context).load(Constans.Display_Image_URL + productDetailsModelArrayList.get(position).getImages().get(0)).placeholder(R.drawable.ic_user_img).into(holder.iv_pro_image);
        } else {
            Glide.with(context).load(R.drawable.ic_user_img).placeholder(R.drawable.ic_user_img).into(holder.iv_pro_image);

        }
        holder.txt_pro_name.setText(productDetailsModelArrayList.get(position).getName());
        holder.txt_pro_des.setText(productDetailsModelArrayList.get(position).getDescription());

        if (productDetailsModelArrayList.get(position).getOffer().equals("")) {
            holder.txt_pro_offer.setVisibility(View.GONE);
        } else {
            holder.txt_pro_offer.setVisibility(View.VISIBLE);
            holder.txt_pro_offer.setText(productDetailsModelArrayList.get(position).getOffer() + "% Off");
        }
        holder.txt_pro_price.setText("$" + String.format("%,.0f", Float.valueOf(productDetailsModelArrayList.get(position).getPrice())) + ".00");

        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(context);
                View view = LayoutInflater.from(context).inflate(R.layout.delete_dialog_product, null);
                dialog.setContentView(view);

                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
                InsetDrawable insetDrawable = new InsetDrawable(back, 50);
                dialog.getWindow().setBackgroundDrawable(insetDrawable);

                ImageView dialog_close = dialog.findViewById(R.id.dialog_close);
                AppCompatButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
                AppCompatButton btn_delete = dialog.findViewById(R.id.btn_delete);

                dialog_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        FileUtils.DeleteProduct(context, Constans.remove_product, productDetailsModelArrayList.get(position).getId());
                        dialog.dismiss();

                        productDetailsModelArrayList.remove(position);
                        notifyDataSetChanged();
                    }
                });
                dialog.show();
            }
        });

        holder.iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, AddProductActivity.class).putExtra("Edit_Pro", context.getResources().getString(R.string.edit_product)).putExtra("pro_id", productDetailsModelArrayList.get(position).getId()));
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra("product", "Product");
                intent.putExtra("ProductDetailsId", productDetailsModelArrayList.get(position).getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productDetailsModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_pro_image;
        TextView txt_pro_name;
        TextView txt_pro_des;
        TextView txt_pro_offer;
        TextView txt_pro_price;
        ImageView iv_delete;
        ImageView iv_edit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_pro_image = itemView.findViewById(R.id.iv_pro_image);
            txt_pro_name = itemView.findViewById(R.id.txt_pro_name);
            txt_pro_des = itemView.findViewById(R.id.txt_pro_des);
            txt_pro_offer = itemView.findViewById(R.id.txt_pro_offer);
            txt_pro_price = itemView.findViewById(R.id.txt_pro_price);
            iv_delete = itemView.findViewById(R.id.iv_delete);
            iv_edit = itemView.findViewById(R.id.iv_edit);

        }
    }
}
