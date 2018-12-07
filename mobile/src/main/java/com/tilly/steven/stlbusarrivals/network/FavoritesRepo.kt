package com.tilly.steven.stlbusarrivals.network

class FavoritesRepo {

    companion object {
        private var mInstance: FavoritesRepo? = null

        fun getInstance(): FavoritesRepo {
            if(mInstance == null){
                mInstance = FavoritesRepo()
            }
            return mInstance!!
        }
    }
}