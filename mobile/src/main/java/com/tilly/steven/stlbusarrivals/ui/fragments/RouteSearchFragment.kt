package com.tilly.steven.stlbusarrivals.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import com.tilly.steven.stlbusarrivals.R
import com.tilly.steven.stlbusarrivals.Utils.toast
import com.tilly.steven.stlbusarrivals.model.RouteList
import com.tilly.steven.stlbusarrivals.network.NetworkCallback
import com.tilly.steven.stlbusarrivals.network.RouteRepo
import com.tilly.steven.stlbusarrivals.ui.activity.StopSearchActivity
import com.tilly.steven.stlbusarrivals.ui.adapter.RouteSearchAdapter


class RouteSearchFragment : androidx.fragment.app.Fragment(), NetworkCallback {

    private lateinit var listView: ListView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_route_search, container, false) as ViewGroup
        listView = v.findViewById<View>(R.id.list_route_search) as ListView
        RouteRepo.getInstance().getRouteList(this)
        return v
    }

    fun refreshList(routeList: RouteList) {
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

    override fun onApiError(error: String) {
        toast(error)
    }

    override fun onRouteListLoaded(routeList: RouteList) {
        refreshList(routeList)
    }
}
