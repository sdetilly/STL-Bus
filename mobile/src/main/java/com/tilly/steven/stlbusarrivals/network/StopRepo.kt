package com.tilly.steven.stlbusarrivals.network

class StopRepo {

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