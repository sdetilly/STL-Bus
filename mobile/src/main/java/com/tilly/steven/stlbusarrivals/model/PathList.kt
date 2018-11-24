package com.tilly.steven.stlbusarrivals.model

import com.google.android.gms.wearable.DataMap
import java.io.Serializable
import java.util.*

/**
 * Created by Steven on 2016-02-03.
 */
class PathList : ArrayList<Path>(), Serializable {

    fun putData(): DataMap {
        val map = DataMap()
        for (i in 0 until this.size) {
            map.putDataMap("path$i", this[i].putData())
        }
        return map
    }
}
