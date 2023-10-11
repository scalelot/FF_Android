package com.festum.festumfield.verstion.firstmodule.screens.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.festum.festumfield.R
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.searchcity.OnSearchCity
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse

class PlaceAutocompleteAdapter (
    context: Context,
    private var onSearchCity : OnSearchCity
) : ArrayAdapter<AutocompletePrediction>(context, R.layout.list_item_search_auto) {

    private val predictions = mutableListOf<AutocompletePrediction>()


    override fun getCount(): Int {
        return predictions.size
    }

    override fun getItem(position: Int): AutocompletePrediction? {
        return predictions[position]
    }

    fun getPlaceId(position: Int): String {
        return predictions[position].placeId
    }

    fun clearPredictions() {
        predictions.clear()
        notifyDataSetChanged()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_item_search_auto, parent, false)

        val search = predictions[position]

        val locationName = view.findViewById<TextView>(R.id.text1)
        val linear = view.findViewById<LinearLayout>(R.id.linear)

        locationName?.text = search.getFullText(null).toString()


        linear.setOnClickListener {

            onSearchCity.onSearchCity(search.getFullText(null).toString())

        }

        return view

    }


    fun setPredictions(result: FindAutocompletePredictionsResponse) {
        predictions.clear()
        predictions.addAll(result.autocompletePredictions)
        notifyDataSetChanged()
        result.autocompletePredictions
    }
}
