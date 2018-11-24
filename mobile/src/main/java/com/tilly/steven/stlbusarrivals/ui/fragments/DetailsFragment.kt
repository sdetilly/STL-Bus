package com.tilly.steven.stlbusarrivals.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.j256.ormlite.android.apptools.OpenHelperManager
import com.j256.ormlite.stmt.PreparedQuery
import com.tilly.steven.stlbusarrivals.R
import com.tilly.steven.stlbusarrivals.VolleySingleton
import com.tilly.steven.stlbusarrivals.XmlParser
import com.tilly.steven.stlbusarrivals.dao.DatabaseHelper
import com.tilly.steven.stlbusarrivals.model.Details
import com.tilly.steven.stlbusarrivals.model.TimeList
import java.sql.SQLException
import java.util.*


/**
 * Created by Steven on 2016-01-28.
 */
class DetailsFragment : Fragment(), Observer {

    private var databaseHelper: DatabaseHelper? = null
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
    lateinit var detailsList: ArrayList<Details>
    private lateinit var mHandler: Handler

    private var mStatusChecker: Runnable = object : Runnable {
        override fun run() {
            sendRequest()
            val mInterval = 60000
            mHandler.postDelayed(this, mInterval.toLong())
        }
    }

    private// Construct the data source
    // get our query builder from the DAO
    // the 'password' field must be equal to "qwerty"
    // prepare our sql statement
    val allOrderedDetails: ArrayList<Details>
        get() {
            val queryBuilder = getHelper().getDetailsDao()!!.queryBuilder()
            var preparedQuery: PreparedQuery<Details>? = null
            try {
                preparedQuery = queryBuilder.prepare()
            } catch (e: SQLException) {
                e.printStackTrace()
            }

            return getHelper().getDetailsDao()!!.query(preparedQuery) as ArrayList<Details>
        }

    //Needed so that databaseHelper can be initialised
    private fun getHelper(): DatabaseHelper {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(activity, DatabaseHelper::class.java)
        }
        return databaseHelper!!
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

        detailsList = allOrderedDetails

        val fab = v.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val details = Details()
            details.tag = routeTag
            details.stopId = stopId
            details.routeName = routeName
            details.stopName = stopName
            getHelper().getDao()!!.create(details)
            Toast.makeText(activity, getString(R.string.added_favorites), Toast.LENGTH_SHORT).show()
            fab.visibility = View.GONE
        }
        for (i in detailsList.indices) {
            if (detailsList[i].stopId == stopId) {
                fab.visibility = View.GONE
            }
        }
        sendMessageRequest()
        return v
    }

    override fun update(observable: Observable, o: Any) {
        if (activity != null) {
            Log.d("detailsfrag update", "entered")
            if (o is TimeList) {
                timeList = o
                val c = Calendar.getInstance()
                var currentHour = c.get(Calendar.HOUR_OF_DAY)
                val currentMinutes = c.get(Calendar.MINUTE)
                if (timeList!!.size != 0) {
                    var predictedMinutes = currentMinutes + Integer.valueOf(timeList!![0].time)
                    while (predictedMinutes >= 60) {
                        currentHour++
                        predictedMinutes -= 60
                    }
                    if (predictedMinutes < 10) {
                        firstPrediction.text = "$currentHour:0$predictedMinutes   ${getString(R.string.next_bus, timeList!![0].time)}"
                    } else {
                        firstPrediction.text = "$currentHour:$predictedMinutes   ${getString(R.string.next_bus, timeList!![0].time)}"
                    }
                    if (timeList!!.size > 1) {
                        var nextHour = c.get(Calendar.HOUR_OF_DAY)
                        val nextMinutes = c.get(Calendar.MINUTE)
                        var nextPredictedMinutes = nextMinutes + Integer.valueOf(timeList!![1].time)
                        while (nextPredictedMinutes >= 60) {
                            nextHour++
                            nextPredictedMinutes -= 60
                        }
                        if (nextPredictedMinutes < 10) {
                            secondPrediction.text = "$nextHour:0$nextPredictedMinutes   ${getString(R.string.other_bus, timeList!![1].time)}"
                        } else {
                            secondPrediction.text = "$nextHour:$nextPredictedMinutes   ${getString(R.string.other_bus, timeList!![1].time)}"
                        }
                    } else
                        secondPrediction.visibility = View.GONE
                }
            } else if (o is String) {
                val text = o + ""
                tvMessage.text = text
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper()
            databaseHelper = null
        }
    }

    companion object {
        private var timeList: TimeList? = null
    }
}// Required empty public constructor
