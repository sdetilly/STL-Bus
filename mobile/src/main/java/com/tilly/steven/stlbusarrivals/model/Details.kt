package com.tilly.steven.stlbusarrivals.model

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.gms.wearable.DataMap
import com.j256.ormlite.field.DatabaseField
import com.tilly.steven.stlbusarrivals.VolleySingleton
import com.tilly.steven.stlbusarrivals.XmlParser
import java.util.*

/**
 * Created by Steven on 2016-01-29.
 */
class Details : Observable(), Observer {

    private val xmlparser = XmlParser()
    var prediction: String? = null
    private var timeList: TimeList? = null

    @DatabaseField(generatedId = true)
    var id: Int = 0
    @DatabaseField
    var tag: String = ""
    @DatabaseField
    var stopId: String = ""
    @DatabaseField
    var routeName: String = ""
    @DatabaseField
    var stopName: String = ""

    fun putData(): DataMap {
        val map = DataMap()
        map.putString("routeTag", tag)
        map.putString("stopId", stopId)
        map.putString("routeName", routeName)
        map.putString("stopName", stopName)
        map.putString("prediction", prediction)
        return map
    }

    fun getNetPrediction(ctx: Context) {
        xmlparser.addObserver(this)
        val queue = VolleySingleton.getInstance(ctx).requestQueue
        val url = "http://webservices.nextbus.com/service/publicXMLFeed?command=predictions&a=stl&stopId=$stopId&routeTag=$tag"
        val request = StringRequest(url, Response.Listener { response ->
            // we got the response, now our job is to handle it
            //parseXmlResponse(response);
            try {
                xmlparser.readPrediction(response)
                Log.d("unitDetails", "This detail is parsing...")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { error ->
            error.printStackTrace()
            getNetPrediction(ctx)
        })
        queue.add(request)
    }

    override fun update(observable: Observable, o: Any) {
        Log.d("unitDetails", "This detail has been updated")
        if (o is TimeList) {
            timeList = o
            if (timeList!!.size > 0) {
                this.prediction = timeList!![0].time

            }
            setChanged()
            notifyObservers(timeList)
            Log.d("unitDetails", "This detail's prediction is : " + this.prediction!!)
        }
    }
}
