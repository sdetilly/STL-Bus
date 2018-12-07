package com.tilly.steven.stlbusarrivals.model

import com.google.android.gms.maps.model.LatLng

/**
 * Created by Steven on 2016-02-04.
 */
data class PathBounds(var latMin: Double = 0.toDouble(),
                      var latMax: Double = 0.toDouble(),
                      var longMin: Double = 0.toDouble(),
                      var longMax: Double = 0.toDouble()) {
    fun getMinBounds(): LatLng = LatLng(latMin, longMin)
    fun getMaxBounds(): LatLng = LatLng(latMax, longMax)
}
