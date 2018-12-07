package com.tilly.steven.stlbusarrivals.network

import com.tilly.steven.stlbusarrivals.model.*

interface NetworkCallback {
    fun onApiError(error: String)
    fun onRouteListLoaded(routeList: RouteList){}
    fun onStopListLoaded(stopList: StopList){}
    fun onPathListLoaded(pathList: PathList){}
    fun onPathBoundsLoaded(pathBounds: PathBounds){}
    fun onVehicleListLoaded(vehiculeList: VehiculeList){}
    fun onDetailsLoaded(details: Details){}
}