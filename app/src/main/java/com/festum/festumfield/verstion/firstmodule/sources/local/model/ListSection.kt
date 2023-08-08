package com.festum.festumfield.verstion.firstmodule.sources.local.model


data class ListSection(
    val title: String,
    val code: String,
    val isToday: Boolean,
    val isPastSection: Boolean
) : ListItem(null) {
    override fun toString(): String {
        return title
    }
}
