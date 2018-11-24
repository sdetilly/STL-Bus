package com.tilly.steven.stlbusarrivals.model

import com.google.android.gms.wearable.DataMap
import java.util.*

/**
 * Created by Steven on 2016-02-04.
 */
class Path : ArrayList<Point>() {

    fun putData(): DataMap {
        val map = DataMap()
        for (i in 0 until this.size) {
            map.putDataMap("point$i", this[i].putData())
        }
        return map
    }
}
