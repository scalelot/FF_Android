package com.festum.festumfield.verstion.firstmodule.sources.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductItem(

	@field:SerializedName("offer")
	val offer: String? = null,

	@field:SerializedName("subCategory")
	val subCategory: String? = null,

	@field:SerializedName("images")
	val images: List<String?>? = null,

	@field:SerializedName("price")
	val price: Int? = null,

	@field:SerializedName("itemCode")
	val itemCode: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("category")
	val category: String? = null

) : Parcelable {
	constructor(parcel: Parcel) : this(
		parcel.readString(),
		parcel.readString(),
		parcel.createStringArrayList(),
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString()
	) {
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(offer)
		parcel.writeString(subCategory)
		parcel.writeStringList(images)
		parcel.writeValue(price)
		parcel.writeString(itemCode)
		parcel.writeString(name)
		parcel.writeString(description)
		parcel.writeString(id)
		parcel.writeString(category)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<ProductItem> {
		override fun createFromParcel(parcel: Parcel): ProductItem {
			return ProductItem(parcel)
		}

		override fun newArray(size: Int): Array<ProductItem?> {
			return arrayOfNulls(size)
		}
	}
}
