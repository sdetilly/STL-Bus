package com.tilly.steven.stlbusarrivals.model


/**
 * Created by Steven on 2016-01-25.
 */
class Route {
    var tag: String = ""

    var title: String = ""

    val name: String
        get() {
            val separated = title.split(" ".toRegex(), 2).toTypedArray()
            return tag + " " + separated[1]
        }
}
