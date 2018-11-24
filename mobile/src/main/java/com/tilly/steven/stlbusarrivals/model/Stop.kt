package com.tilly.steven.stlbusarrivals.model

/**
 * Created by Steven on 2016-01-28.
 */
class Stop {
    var tag: String = ""

    var title: String = ""

    var id: String = ""

    val name: String
        get() = "$tag $title"
}
