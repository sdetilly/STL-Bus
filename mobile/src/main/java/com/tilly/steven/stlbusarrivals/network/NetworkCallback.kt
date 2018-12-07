package com.tilly.steven.stlbusarrivals.network

import com.tilly.steven.stlbusarrivals.model.Details
import com.tilly.steven.stlbusarrivals.model.RouteList
import com.tilly.steven.stlbusarrivals.model.Stop

interface NetworkCallback {
    fun onApiError(error: String)
    fun onRouteListLoaded(routeList: RouteList){}
    fun onStopListLoaded(stopList: List<Stop>){}
    fun onDetailsLoaded(details: Details){}
}