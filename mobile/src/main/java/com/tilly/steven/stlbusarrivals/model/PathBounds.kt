package com.tilly.steven.stlbusarrivals.model

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.wearable.DataMap

/**
 * Created by Steven on 2016-02-04.
 */
class PathBounds {

    var latMin: Double = 0.toDouble()
    var latMax: Double = 0.toDouble()
    var longMin: Double = 0.toDouble()
    var longMax: Double = 0.toDouble()

    val minBounds: LatLng
        get() = LatLng(latMin, longMin)

    val maxBounds: LatLng
        get() = LatLng(latMax, longMax)

    fun putData(): DataMap {
        val map = DataMap()
        map.putDouble("latMin", latMin)
        map.putDouble("latMax", latMax)
        map.putDouble("longMin", longMin)
        map.putDouble("longMax", longMax)
        return map
    }
}
