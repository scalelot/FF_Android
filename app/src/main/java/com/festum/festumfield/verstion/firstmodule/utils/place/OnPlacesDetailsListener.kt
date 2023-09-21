package com.festum.festumfield.verstion.firstmodule.utils.place

interface OnPlacesDetailsListener {

    fun onPlaceDetailsFetched(placeDetails: PlaceDetails)

    /**
     * Triggered when there is an error and passes the error message along as a parameter
     */
    fun onError(errorMessage: String)

}