package com.tilly.steven.stlbusarrivals.network

class DetailsRepo {


    fun getPath(routeTag: String, callBack: NetworkCallback){
        fetchPath(routeTag, callBack)
    }

    private fun fetchPath(routeTag: String, callBack: NetworkCallback){
        StlApi.getInstance().readPath(routeTag, callBack)
    }

    fun getLocation(routeTag: String, callBack: NetworkCallback){
        fetchLocation(routeTag, callBack)
    }

    private fun fetchLocation(routeTag: String, callBack: NetworkCallback){
        StlApi.getInstance().getLocation(routeTag, callBack)
    }

    companion object {
        private var mInstance: DetailsRepo? = null

        fun getInstance(): DetailsRepo {
            if(mInstance == null){
                mInstance = DetailsRepo()
            }
            return mInstance!!
        }
    }
}