package com.tilly.steven.stlbusarrivals.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.tilly.steven.stlbusarrivals.R
import com.tilly.steven.stlbusarrivals.VolleySingleton
import com.tilly.steven.stlbusarrivals.XmlParser
import com.tilly.steven.stlbusarrivals.model.StopList
import com.tilly.steven.stlbusarrivals.ui.adapter.StopSearchAdapter
import java.util.*

/**
 * Created by Steven on 2016-01-28.
 */
class StopSearchActivity : AppCompatActivity(), Observer {

    private lateinit var listView: ListView
    private val xmlparser = XmlParser()
    private lateinit var routeTag: String
    private lateinit var routeName: String
    private lateinit var stopList: StopList

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop_search)
        listView = findViewById<View>(R.id.list_stop_search) as ListView
        if (savedInstanceState == null) {
            val extras = intent.extras
            routeTag = extras?.getString("tag") ?: ""
            routeName = extras?.getString("routeName") ?: ""
        } else {
            routeTag = savedInstanceState.getString("tag", "")
            routeName = savedInstanceState.getString("routeName", "")
        }
        title = routeName
        sendRequest()
    }

    private fun sendRequest() {
        val queue = VolleySingleton.getInstance(this).requestQueue
        xmlparser.addObserver(this)
        val url = "http://webservices.nextbus.com/service/publicXMLFeed?command=routeConfig&a=stl&r=$routeTag&terse"
        val request = StringRequest(url, Response.Listener { response ->
            // we got the response, now our job is to handle it
            //parseXmlResponse(response);
            try {
                xmlparser.readStopXml(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { error ->
            error.printStackTrace()
            sendRequest()
        })
        queue.add(request)
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putString("routeTag", routeTag)
    }

    fun refreshList() {
        val stopSearchAdapter = StopSearchAdapter(this, R.layout.row_stop_search_list, stopList)
        listView.adapter = stopSearchAdapter
        stopSearchAdapter.notifyDataSetChanged()

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            val item = stopList[position]
            val stopId = item.id
            val stopName = item.title

            val stopSearchIntent = Intent(baseContext, StopDetailsActivity::class.java)
            stopSearchIntent.putExtra("stopId", stopId)
            stopSearchIntent.putExtra("routeTag", routeTag)
            stopSearchIntent.putExtra("routeName", routeName)
            stopSearchIntent.putExtra("stopName", stopName)
            startActivity(stopSearchIntent)
        }
    }

    override fun update(observable: Observable, o: Any) {
        Log.d("stopfrag update", "entered")
        if (o is StopList) {
            stopList = o
        }
        refreshList()
    }
}
