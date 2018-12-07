package com.tilly.steven.stlbusarrivals.network

import com.tilly.steven.stlbusarrivals.model.RouteList

class RouteRepo {

    private var routeList: RouteList? = null

    fun getRouteList(callBack: NetworkCallback){
        if(routeList != null){
            callBack.onRouteListLoaded(routeList ?: RouteList())
            return
        }
        fetchRouteList(callBack)
    }

    private fun fetchRouteList(callBack: NetworkCallback){
        StlApi.getInstance().getRouteList(callBack)
    }

    companion object {
        private var mInstance: RouteRepo? = null

        fun getInstance(): RouteRepo {
            if(mInstance == null){
                mInstance = RouteRepo()
            }
            return mInstance!!
        }
    }
}