package com.tilly.steven.stlbusarrivals.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.tilly.steven.stlbusarrivals.R
import com.tilly.steven.stlbusarrivals.VolleySingleton
import com.tilly.steven.stlbusarrivals.XmlParser
import com.tilly.steven.stlbusarrivals.model.RouteList
import com.tilly.steven.stlbusarrivals.ui.activity.StopSearchActivity
import com.tilly.steven.stlbusarrivals.ui.adapter.RouteSearchAdapter
import java.util.*


class RouteSearchFragment : androidx.fragment.app.Fragment(), Observer {

    private lateinit var listView: ListView
    private val xmlparser = XmlParser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        xmlparser.addObserver(this)
        sendRequest()
    }

    private fun sendRequest() {
        activity?.let {
            val queue = VolleySingleton.getInstance(it).requestQueue
            val url = "http://webservices.nextbus.com/service/publicXMLFeed?command=routeList&a=stl"
            val request = StringRequest(url, Response.Listener { response ->
                // we got the response, now our job is to handle it
                //parseXmlResponse(response);
                try {
                    xmlparser.readRouteXml(response)
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_route_search, container, false) as ViewGroup
        listView = v.findViewById<View>(R.id.list_route_search) as ListView
        return v
    }

    fun refreshList() {
        activity?.let {
            val routeSearchAdapter = RouteSearchAdapter(it, R.layout.row_route_search_list, routeList)
            listView.adapter = routeSearchAdapter
            routeSearchAdapter.notifyDataSetChanged()

            listView.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
                val item = routeList[position]
                val tag = item.tag
                val routeName = item.title
                val stopSearchIntent = Intent(it, StopSearchActivity::class.java)
                stopSearchIntent.putExtra("tag", tag)
                stopSearchIntent.putExtra("routeName", routeName)
                it.startActivity(stopSearchIntent)
            }
        }

    }

    override fun update(observable: Observable, o: Any) {
        activity?.let {
            Log.d("searchfrag update", "entered")
            if (o is RouteList) {
                routeList = o
            }
            refreshList()
        }

    }

    companion object {
        private lateinit var routeList: RouteList
    }
}
