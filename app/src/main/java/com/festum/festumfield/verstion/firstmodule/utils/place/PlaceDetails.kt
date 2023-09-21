package com.festum.festumfield.verstion.firstmodule.utils.place

class PlaceDetails(
    val name: String,
    val address: ArrayList<Address>,
    val lat: Double,
    val lng: Double,
    val placeId: String,
    val url: String,
    val utcOffset: Int,
    val vicinity: String,
    val compoundPlusCode: String,
    val globalPlusCode: String
)