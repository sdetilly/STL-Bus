package com.tilly.steven.stlbusarrivals.network

import com.tilly.steven.stlbusarrivals.model.StopList

class StopRepo {

    private var stopList: StopList? = null
    private var routeTag: String? = null

    fun getStopList(routeTag: String, callBack: NetworkCallback){
        if(stopList != null && this.routeTag == routeTag){
            callBack.onStopListLoaded(stopList ?: StopList())
            return
        }
        this.routeTag = routeTag
        fetchStopList(routeTag, callBack)
    }

    private fun fetchStopList(routeTag:String, callBack: NetworkCallback){
        StlApi.getInstance().getStopList(routeTag, callBack)
    }

    companion object {
        private var mInstance: StopRepo? = null

        fun getInstance(): StopRepo {
            if(mInstance == null){
                mInstance = StopRepo()
            }
            return mInstance!!
        }
    }
}