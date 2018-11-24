package com.tilly.steven.stlbusarrivals.model

import com.google.android.gms.wearable.DataMap

/**
 * Created by Steven on 2016-01-28.
 */
class Vehicule {

    var longitude: String = ""

    var latitude: String = ""

    fun putData(): DataMap {
        val map = DataMap()
        map.putString("longitude", longitude)
        map.putString("latitude", latitude)
        return map
    }
}
