package com.tilly.steven.stlbusarrivals

import android.content.Context

import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

/**
 * Created by Steven on 2016-01-22.
 */
class VolleySingleton private constructor(context: Context) {
    val requestQueue: RequestQueue = Volley.newRequestQueue(context)

    companion object {

        private var instance: VolleySingleton? = null

        fun getInstance(context: Context): VolleySingleton {
            if (instance == null) {
                instance = VolleySingleton(context)
            }
            return instance!!
        }
    }
}
