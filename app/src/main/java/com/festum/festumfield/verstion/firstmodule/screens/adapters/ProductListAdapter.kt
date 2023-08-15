package com.festum.festumfield.verstion.firstmodule.screens.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.festum.festumfield.R
import com.festum.festumfield.databinding.ItemAllProductDisplayBinding
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.ProductItemInterface
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsProducts

class ProductListAdapter(
    private val context: Context,
    private val productList : ArrayList<FriendsProducts>,
    private val productInterface : ProductItemInterface) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(ItemAllProductDisplayBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = viewHolder as ViewHolder
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    inner class ViewHolder(private val displayBinding: ItemAllProductDisplayBinding) : RecyclerView.ViewHolder(displayBinding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            val item = productList[position]

            displayBinding.txtTitleName.text = item.name
            displayBinding.txtDes.text = item.description
            displayBinding.txtPrice.text = "${"$" + item.price.toString() + ".00"}"


            val options = RequestOptions()
            displayBinding.ivImage.clipToOutline = true

            val image = item.images?.get(0)

            Glide.with(context.applicationContext)
                .load(image)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.headpone)
                .apply(
                    options.centerCrop()
                        .skipMemoryCache(true)
                        .priority(Priority.HIGH)
                        .format(DecodeFormat.PREFER_ARGB_8888)
                )
                .into(displayBinding.ivImage)

            displayBinding.container.setOnClickListener {
                item.id?.let { it1 -> productInterface.singleProduct(productList[position], it1, false) }
            }

        }

    }

}