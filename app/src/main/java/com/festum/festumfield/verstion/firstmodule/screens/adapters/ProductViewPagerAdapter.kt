package com.festum.festumfield.verstion.firstmodule.screens.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.festum.festumfield.R
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.Product
import com.festum.festumfield.verstion.firstmodule.utils.ProductImageMediaDiffCallback

class ProductViewPagerAdapter(val context: Context) : ListAdapter<String, ProductViewPagerAdapter.ProductViewHolder>(ProductImageMediaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =

        ProductViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_layout_viewslider, parent, false))

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(item: String) {

            Glide.with(context)
                .load(item)
                .placeholder(R.mipmap.ic_app_logo)
                .into(imageView)

        }
    }

}