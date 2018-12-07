package com.tilly.steven.stlbusarrivals.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tilly.steven.stlbusarrivals.*
import com.tilly.steven.stlbusarrivals.Utils.toast
import com.tilly.steven.stlbusarrivals.model.Details
import com.tilly.steven.stlbusarrivals.model.TimeList
import java.util.*


/**
 * Created by Steven on 2016-01-28.
 */
class DetailsFragment : Fragment(), Observer {

    private val xmlparser = XmlParser()
    private var stopId: String = ""
    private var routeTag: String = ""
    private var stopName: String = ""
    private var routeName: String = ""
    lateinit var tv_routeName: TextView
    lateinit var tv_stopName: TextView
    lateinit var firstPrediction: TextView
    lateinit var secondPrediction: TextView
    lateinit var tvMessage: TextView
    lateinit var detailsList: List<Details>
    private lateinit var mHandler: Handler
    lateinit var fab: FloatingActionButton

    private var mStatusChecker: Runnable = object : Runnable {
        override fun run() {
            sendRequest()
            val mInterval = 60000
            mHandler.postDelayed(this, mInterval.toLong())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        xmlparser.addObserver(this)
        stopId = arguments?.getString("stopId") ?: ""
        stopName = arguments?.getString("stopName") ?: ""
        routeTag = arguments?.getString("routeTag") ?: ""
        routeName = arguments?.getString("routeName") ?: ""
        mHandler = Handler()
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_details, container, false) as ViewGroup
        tv_routeName = v.findViewById(R.id.tv_details_route_name)
        tv_stopName = v.findViewById(R.id.tv_details_stop_name)
        firstPrediction = v.findViewById(R.id.first_prediction)
        secondPrediction = v.findViewById(R.id.second_prediction)
        tvMessage = v.findViewById(R.id.tv_messages)
        tv_routeName.text = routeName
        tv_stopName.text = stopName

        fab = v.findViewById(R.id.fab)
        fab.setOnClickListener {
            val details = Details()
            details.tag = routeTag
            details.stopId = stopId
            details.routeName = routeName
            details.stopName = stopName
            launchUI {
                asyncAwait { DetailsDatabase.getInstance().detailsDao().insertOrUpdate(details) }
                toast(message = getString(R.string.added_favorites))
                fab.visibility = View.GONE
            }
        }
        DetailsDatabase.getInstance().detailsDao().loadDetails().observe(this, androidx.lifecycle.Observer {
            detailsList = it
            for (i in detailsList.indices) {
                if (detailsList[i].stopId == stopId) {
                    fab.visibility = View.GONE
                }
            }
        })
        sendMessageRequest()
        return v
    }

    override fun onResume() {
        super.onResume()
        startRepeatingTask()
    }

    private fun startRepeatingTask() {
        mStatusChecker.run()
    }

    override fun onStop() {
        super.onStop()
        mHandler.removeCallbacks(mStatusChecker)
    }

    private fun sendRequest() {
        activity?.let {
            val queue = VolleySingleton.getInstance(it).requestQueue
            val url = "http://webservices.nextbus.com/service/publicXMLFeed?command=predictions&a=stl&stopId=$stopId&routeTag=$routeTag"
            val request = StringRequest(url, Response.Listener { response ->
                // we got the response, now our job is to handle it
                //parseXmlResponse(response);
                try {
                    xmlparser.readPrediction(response)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->
                error.printStackTrace()
                sendRequest()
            })
            queue.add(request)
        }
    }

    private fun sendMessageRequest() {
        activity?.let {
            val queue = VolleySingleton.getInstance(it).requestQueue
            val url = "http://webservices.nextbus.com/service/publicXMLFeed?command=messages&a=stl&r=$routeTag"
            val request = StringRequest(url, Response.Listener { response ->
                // we got the response, now our job is to handle it
                //parseXmlResponse(response);
                try {
                    xmlparser.readMessages(response)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->
                error.printStackTrace()
                sendMessageRequest()
            })
            queue.add(request)
        }
    }

    override fun update(observable: Observable, o: Any) {
        if (activity != null) {
            Log.d("detailsfrag update", "entered")
            if (o is TimeList) {
                val c = Calendar.getInstance()
                var currentHour = c.get(Calendar.HOUR_OF_DAY)
                val currentMinutes = c.get(Calendar.MINUTE)
                if (o.size != 0) {
                    var predictedMinutes = currentMinutes + Integer.valueOf(o[0].time)
                    while (predictedMinutes >= 60) {
                        currentHour++
                        predictedMinutes -= 60
                    }
                    if (predictedMinutes < 10) {
                        firstPrediction.text = "$currentHour:0$predictedMinutes   ${getString(R.string.next_bus, o[0].time)}"
                    } else {
                        firstPrediction.text = "$currentHour:$predictedMinutes   ${getString(R.string.next_bus, o[0].time)}"
                    }
                    if (o.size > 1) {
                        var nextHour = c.get(Calendar.HOUR_OF_DAY)
                        val nextMinutes = c.get(Calendar.MINUTE)
                        var nextPredictedMinutes = nextMinutes + Integer.valueOf(o[1].time)
                        while (nextPredictedMinutes >= 60) {
                            nextHour++
                            nextPredictedMinutes -= 60
                        }
                        if (nextPredictedMinutes < 10) {
                            secondPrediction.text = "$nextHour:0$nextPredictedMinutes   ${getString(R.string.other_bus, o[1].time)}"
                        } else {
                            secondPrediction.text = "$nextHour:$nextPredictedMinutes   ${getString(R.string.other_bus, o[1].time)}"
                        }
                    } else
                        secondPrediction.visibility = View.GONE
                }
            } else if (o is String) {
                tvMessage.text = o
            }
        }
    }
}
