package com.tilly.steven.stlbusarrivals.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.tilly.steven.stlbusarrivals.R
import com.tilly.steven.stlbusarrivals.Utils.toast
import com.tilly.steven.stlbusarrivals.model.StopList
import com.tilly.steven.stlbusarrivals.network.NetworkCallback
import com.tilly.steven.stlbusarrivals.network.StopRepo
import com.tilly.steven.stlbusarrivals.ui.adapter.StopSearchAdapter

/**
 * Created by Steven on 2016-01-28.
 */
class StopSearchActivity : AppCompatActivity(), NetworkCallback {

    private lateinit var listView: ListView
    private lateinit var routeTag: String
    private lateinit var routeName: String

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
        StopRepo.getInstance().getStopList(routeTag, this)
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putString("routeTag", routeTag)
    }

    fun refreshList(stopList: StopList) {
        val stopSearchAdapter = StopSearchAdapter(this, R.layout.row_stop_search_list, stopList)
        listView.adapter = stopSearchAdapter
        stopSearchAdapter.notifyDataSetChanged()

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
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

    override fun onApiError(error: String) {
        toast(error)
    }

    override fun onStopListLoaded(stopList: StopList) {
        refreshList(stopList)
    }
}
