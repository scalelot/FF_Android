package com.festum.festumfield.verstion.firstmodule.screens.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.festum.festumfield.R
import com.festum.festumfield.verstion.firstmodule.sources.local.model.PhoneCodeModel
import com.festum.festumfield.verstion.firstmodule.utils.CountryCityUtils
import java.util.*


class CountryCodePickerAdapter(

    val context: Context,
    var countries: ArrayList<PhoneCodeModel>,
    private val countryPick: CountyrPick
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var countryFilterList = ArrayList<PhoneCodeModel>()

    init {
        countryFilterList = countries
    }

    interface CountyrPick {

        fun pick(countries: PhoneCodeModel)

    }

    class Holder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val countryName: TextView = itemView.findViewById(R.id.country_name_tv)
        val flagImv: TextView = itemView.findViewById(R.id.flag_imv)
        val codeTv: TextView = itemView.findViewById(R.id.code_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.country_code, parent, false)
        return Holder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {

        val holder = holder as Holder

        val ch = CountryCityUtils.firstTwo(countryFilterList[position].key.toString())

        holder.flagImv.text =
            CountryCityUtils.getFlagId(ch.lowercase(Locale.getDefault()).toString())

        holder.codeTv.text = "+" + countryFilterList[position].value

        holder.itemView.setOnClickListener {
            countryPick.pick(countryFilterList[position])
        }

        val loc = Locale("", ch)
        holder.countryName.text = loc.displayCountry

    }


    override fun getItemCount(): Int {
        return countryFilterList.size
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val filteredList1: MutableList<PhoneCodeModel> = ArrayList()
                if (charSequence.toString().isEmpty()) {
                    filteredList1.addAll(countries)
                } else {
                    for (location in countries) {
                        val ch = CountryCityUtils.firstTwo(location.key.toString())
                        val loc = Locale("", ch)

                        if (loc.displayCountry.lowercase().contains(
                                charSequence.toString().lowercase()
                            ) || location.value?.contains(charSequence.toString()) == true
                        ) {
                            filteredList1.add(location)
                        }
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = filteredList1
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                countryFilterList = filterResults.values as ArrayList<PhoneCodeModel>
                notifyDataSetChanged()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun AddAll(phoneModelList: ArrayList<PhoneCodeModel>) {
        countryFilterList = ArrayList<PhoneCodeModel>()
        countries = ArrayList<PhoneCodeModel>()

        countryFilterList.addAll(phoneModelList)
        countries.addAll(phoneModelList)
        notifyDataSetChanged()
    }


}