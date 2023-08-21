package com.festum.festumfield.verstion.firstmodule.utils

import androidx.recyclerview.widget.DiffUtil

class ProductImageMediaDiffCallback : DiffUtil.ItemCallback<String>() {

    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem== newItem

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem

}