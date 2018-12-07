package com.tilly.steven.stlbusarrivals.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.PopupMenu
import com.android.volley.RequestQueue
import com.tilly.steven.stlbusarrivals.*
import com.tilly.steven.stlbusarrivals.model.Details
import com.tilly.steven.stlbusarrivals.ui.activity.StopDetailsActivity
import com.tilly.steven.stlbusarrivals.ui.adapter.DetailsAdapter
import java.util.*


class FavoritesFragment : androidx.fragment.app.Fragment(), Observer {

    lateinit var queue: RequestQueue
    lateinit var listView: ListView
    lateinit var detailsAdapter: DetailsAdapter
    var detailsList: MutableList<Details>? = null
    private var lastClickedDetailsId: Int = 0
    private lateinit var mHandler: Handler

    var mStatusChecker: Runnable = object : Runnable {
        override fun run() {
            getDetailPrediction()
            val mInterval = 60000
            mHandler.postDelayed(this, mInterval.toLong())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        queue = VolleySingleton.getInstance(activity!!).requestQueue
        mHandler = Handler()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_favorites, container, false) as ViewGroup
        listView = v.findViewById(R.id.list_favorite)

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val item = detailsList!![position]
            val stopId = item.stopId
            val stopName = item.stopName
            val routeTag = item.tag
            val routeName = item.routeName

            val stopSearchIntent = Intent(activity, StopDetailsActivity::class.java)
            stopSearchIntent.putExtra("stopId", stopId)
            stopSearchIntent.putExtra("routeTag", routeTag)
            stopSearchIntent.putExtra("routeName", routeName)
            stopSearchIntent.putExtra("stopName", stopName)
            startActivity(stopSearchIntent)
        }
        listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position) as Details
            val popup = PopupMenu(activity, view)
            //Inflating the Popup using xml file
            popup.menuInflater.inflate(R.menu.menu_favorites, popup.menu)
            lastClickedDetailsId = item.id

            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener { item2 ->
                val id2 = item2.itemId

                if (id2 == R.id.item_delete) {
                    Log.d("MainActivity", "will delete timer...")
                    deleteDetails(item)
                }
                true
            }
            popup.show()
            true
        }
        return v
    }

    override fun onResume() {
        super.onResume()
        DetailsDatabase.getInstance().detailsDao().loadDetails().observe(this, androidx.lifecycle.Observer {
            detailsList = it.toMutableList()
            detailsAdapter = DetailsAdapter(activity!!, R.layout.row_favorites, detailsList ?: emptyList())

        })
        startRepeatingTask()
    }

    private fun startRepeatingTask() {
        mStatusChecker.run()
    }

    override fun onStop() {
        super.onStop()
        mHandler.removeCallbacks(mStatusChecker)
    }

    private fun getDetailPrediction() {
        val list = detailsList
        if (list?.isNotEmpty() == true) {
            for (i in list.indices) {
                Log.d("favoritefrag", "adding observers...")
                list[i].addObserver(this)
                list[i].getNetPrediction(activity!!)
            }
        } else {
            detailsList?.clear()
        }
    }

    private fun deleteDetails(details: Details) {
        launchUI {
            asyncAwait { DetailsDatabase.getInstance().detailsDao().deleteDetail(details) }
        }
        getDetailPrediction()
    }

    override fun update(observable: Observable, o: Any) {
        activity?.let {
            Log.d("favoritesfrag update", "entered")
            detailsAdapter.notifyDataSetChanged()
            listView.adapter = detailsAdapter
            detailsAdapter.notifyDataSetChanged()
        }
    }
}
