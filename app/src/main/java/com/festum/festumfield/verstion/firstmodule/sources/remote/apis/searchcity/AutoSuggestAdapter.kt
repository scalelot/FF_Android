package com.festum.festumfield.verstion.firstmodule.sources.remote.apis.searchcity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.TextView
import com.festum.festumfield.R
import java.util.Locale

class AutoSuggestAdapter(
    private var context: Activity,
    resource: Int,
    textViewResourceId: Int,
    private var mListData: ArrayList<ResponseSearch>,
    private var onSearchCity : OnSearchCity
) :
    ArrayAdapter<ResponseSearch?>(
        context,
        resource,
        textViewResourceId,
        mListData as List<ResponseSearch?>
    ), Filterable {

    private val mTempItem: ArrayList<ResponseSearch>? = null
    private var mSuggestion = arrayListOf<ResponseSearch>()

    fun setData(list: ArrayList<ResponseSearch>) {
        mListData.clear()
        mListData.addAll(list)
    }

    override fun getCount(): Int {
        return mListData.size
    }

    override fun getItem(position: Int): ResponseSearch {
        return mListData[position]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    @SuppressLint("SetTextI18n", "ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_item_search_auto, parent, false)

        val search = mListData[position]

        val locationName = view.findViewById<TextView>(R.id.text1)
        val linear = view.findViewById<LinearLayout>(R.id.linear)

        locationName?.text = search.localizedName + "," + search.administrativeArea?.localizedName + "," + search.country?.localizedName


        linear.setOnClickListener {

            onSearchCity.onSearchCity(search.localizedName + "," + search.administrativeArea?.localizedName + "," + search.country?.localizedName)

        }

        return view

    }

    override fun getFilter(): Filter {
        return nameFilter
    }

    var nameFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any): String? {
            return (resultValue as ResponseSearch).localizedName
        }

        override fun performFiltering(constraint: CharSequence): FilterResults {
            return run {
                mSuggestion.clear()
                if (mTempItem != null) {
                    for (search in mTempItem) {
                        val name: String? = search.localizedName
                        if (name?.lowercase(Locale.getDefault())?.contains(
                                constraint.toString().lowercase(
                                    Locale.getDefault()
                                )
                            ) == true
                        ) {
                            mSuggestion.add(search)
                        }
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = mSuggestion
                filterResults.count = mSuggestion.size
                filterResults
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if (results != null && results.count > 0) {
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }
    }

    init {
        mListData = ArrayList()
        mSuggestion = ArrayList()
    }

}