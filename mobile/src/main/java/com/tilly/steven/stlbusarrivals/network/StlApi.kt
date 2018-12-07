package com.tilly.steven.stlbusarrivals.network

import android.content.Context
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.tilly.steven.stlbusarrivals.MyApplication
import com.tilly.steven.stlbusarrivals.VolleySingleton
import com.tilly.steven.stlbusarrivals.XmlParser

class StlApi {

    fun getRouteList(callback: NetworkCallback){
        val ctx: Context = MyApplication.context
        val queue = VolleySingleton.getInstance(ctx).requestQueue
        val url = "http://webservices.nextbus.com/service/publicXMLFeed?command=routeList&a=stl"
        val request = StringRequest(url, Response.Listener { response ->
            // we got the response, now our job is to handle it
            //parseXmlResponse(response);
            try {
                XmlParser().readRouteXml(response, callback)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { error ->
            error.printStackTrace()
        })
        queue.add(request)
    }

    companion object {
        private var mInstance: StlApi? = null

        fun getInstance(): StlApi {
            if(mInstance == null){
                mInstance = StlApi()
            }
            return mInstance!!
        }
    }
}