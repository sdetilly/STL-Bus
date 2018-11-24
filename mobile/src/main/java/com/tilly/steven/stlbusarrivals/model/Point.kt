package com.tilly.steven.stlbusarrivals.model

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.wearable.DataMap

import java.io.Serializable

/**
 * Created by Steven on 2016-02-03.
 */
class Point : Serializable {

    var longitude: Double = 0.toDouble()
    var latitude: Double = 0.toDouble()

    val latLng: LatLng
        get() = LatLng(latitude, longitude)

    fun putData(): DataMap {
        val map = DataMap()
        map.putDouble("longitude", longitude)
        map.putDouble("latitude", latitude)
        return map
    }
}
