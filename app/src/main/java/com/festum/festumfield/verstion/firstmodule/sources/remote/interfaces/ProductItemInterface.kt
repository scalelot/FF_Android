package com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces

import androidx.viewpager2.widget.ViewPager2
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsProducts

interface ProductItemInterface {

    fun singleProduct(item : FriendsProducts, productId : String, sendProduct : Boolean)

    fun chatProduct(item : FriendsProducts)

}