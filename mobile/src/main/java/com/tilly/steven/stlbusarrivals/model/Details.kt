package com.tilly.steven.stlbusarrivals.model

import android.content.Context
import android.util.Log
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.tilly.steven.stlbusarrivals.VolleySingleton
import com.tilly.steven.stlbusarrivals.XmlParser
import java.util.*

/**
 * Created by Steven on 2016-01-29.
 */
@Entity(tableName = "detail")
class Details : Observable(), Observer {

    @Ignore
    private val xmlparser = XmlParser()

    @Ignore
    var prediction: String? = null

    @Ignore
    private var timeList: TimeList? = null

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var tag: String = ""
    var stopId: String = ""
    var routeName: String = ""
    var stopName: String = ""

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
