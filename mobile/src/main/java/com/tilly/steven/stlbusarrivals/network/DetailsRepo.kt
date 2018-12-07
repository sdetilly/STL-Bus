package com.tilly.steven.stlbusarrivals.network

class DetailsRepo {

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