package com.festum.festumfield.TagView;

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import java.util.*

abstract class FilteredArrayAdapter<T>
(
    context: Context,
    resource: Int,
    textViewResourceId: Int,
    objects: List<T>
) : ArrayAdapter<T>(
    context, resource, textViewResourceId, ArrayList(objects)
) {
    private val originalObjects: List<T> = objects
    private var filter: Filter? = null

    constructor(context: Context, resource: Int, objects: Array<T>) : this(
        context,
        resource,
        0,
        objects
    )

    constructor(
        context: Context,
        resource: Int,
        textViewResourceId: Int,
        objects: Array<T>
    ) : this(context, resource, textViewResourceId, ArrayList<T>(listOf(*objects)))

    @Suppress("unused")
    constructor(context: Context, resource: Int, objects: List<T>) : this(
        context,
        resource,
        0,
        objects
    )

    override fun getFilter(): Filter {
        if (filter == null) filter = AppFilter()
        return filter!!
    }

    protected abstract fun keepObject(obj: T, mask: String?): Boolean

    private inner class AppFilter : Filter() {
        override fun performFiltering(chars: CharSequence?): FilterResults {
            val sourceObjects = ArrayList(originalObjects)
            val result = FilterResults()
            if (chars != null && chars.isNotEmpty()) {
                val mask = chars.toString()
                val keptObjects = ArrayList<T>()
                for (sourceObject in sourceObjects) {
                    if (keepObject(sourceObject, mask)) keptObjects.add(sourceObject)
                }
                result.count = keptObjects.size
                result.values = keptObjects
            } else {
                result.values = sourceObjects
                result.count = sourceObjects.size
            }
            return result
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            clear()
            if (results.count > 0) {
                @Suppress("unchecked_cast")
                this@FilteredArrayAdapter.addAll(results.values as Collection<T>)
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }
    }
}