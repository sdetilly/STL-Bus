package com.tilly.steven.stlbusarrivals.model


/**
 * Created by Steven on 2016-01-25.
 */
data class Route(var tag: String = "",
                 var title: String = "") {

    fun getName(): String {
        val separated = title.split(" ".toRegex(), 2).toTypedArray()
        return tag + " " + separated[1]
    }
}
