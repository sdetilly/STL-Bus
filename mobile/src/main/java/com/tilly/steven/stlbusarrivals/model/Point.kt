package com.tilly.steven.stlbusarrivals.model

import com.google.android.gms.maps.model.LatLng

/**
 * Created by Steven on 2016-02-03.
 */
data class Point(var longitude: Double = 0.toDouble(),
                 var latitude: Double = 0.toDouble()) {

    fun getLatLng(): LatLng = LatLng(latitude, longitude)
}
