package com.tilly.steven.stlbusarrivals.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.PopupMenu
import com.android.volley.RequestQueue
import com.j256.ormlite.android.apptools.OpenHelperManager
import com.j256.ormlite.stmt.PreparedQuery
import com.tilly.steven.stlbusarrivals.R
import com.tilly.steven.stlbusarrivals.VolleySingleton
import com.tilly.steven.stlbusarrivals.dao.DatabaseHelper
import com.tilly.steven.stlbusarrivals.model.Details
import com.tilly.steven.stlbusarrivals.ui.activity.StopDetailsActivity
import com.tilly.steven.stlbusarrivals.ui.adapter.DetailsAdapter
import java.sql.SQLException
import java.util.*


class FavoritesFragment : Fragment(), Observer {

    lateinit var queue: RequestQueue
    private var databaseHelper: DatabaseHelper? = null
    lateinit var listView: ListView
    lateinit var detailsAdapter: DetailsAdapter
    var detailsList: ArrayList<Details>? = null
    private var lastClickedDetailsId: Int = 0
    private lateinit var mHandler: Handler

    private// Construct the data source
    // get our query builder from the DAO
    // the 'password' field must be equal to "qwerty"
    // prepare our sql statement
    val allOrderedDetails: ArrayList<Details>
        get() {
            val queryBuilder = helper.getDetailsDao()?.queryBuilder()
            var preparedQuery: PreparedQuery<Details>? = null
            try {
                preparedQuery = queryBuilder?.prepare()
            } catch (e: SQLException) {
                e.printStackTrace()
            }

            return helper.getDetailsDao()!!.query(preparedQuery) as ArrayList<Details>
        }
    private val helper: DatabaseHelper
        get() {
            if (databaseHelper == null) {
                databaseHelper = OpenHelperManager.getHelper(activity, DatabaseHelper::class.java)
            }
            return databaseHelper!!
        }

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
                    deleteDetails()
                }
                true
            }
            popup.show()
            true
        }
        return v
    }

    override fun onDestroy() {
        super.onDestroy()
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper()
            databaseHelper = null
        }
    }

    override fun onResume() {
        super.onResume()
        detailsList = allOrderedDetails
        startRepeatingTask()
        detailsAdapter = DetailsAdapter(activity!!, R.layout.row_favorites, detailsList!!)
    }

    private fun startRepeatingTask() {
        mStatusChecker.run()
    }

    override fun onStop() {
        super.onStop()
        mHandler.removeCallbacks(mStatusChecker)
    }

    private fun getDetailPrediction() {
        if (detailsList!!.size > 0 && detailsList != null) {
            for (i in detailsList!!.indices) {
                Log.d("favoritefrag", "adding observers...")
                detailsList!![i].addObserver(this)
                detailsList!![i].getNetPrediction(activity!!)
            }
        } else {
            detailsList!!.clear()
        }
    }

    private fun deleteDetails() {
        helper.getDetailsDao()!!.deleteById(this.lastClickedDetailsId)
        detailsList = allOrderedDetails
        //detailsAdapter = null
        getDetailPrediction()
        detailsAdapter = DetailsAdapter(activity!!, R.layout.row_favorites, detailsList!!)
    }

    override fun update(observable: Observable, o: Any) {
        if (activity != null) {
            Log.d("favoritesfrag update", "entered")
            detailsAdapter.notifyDataSetChanged()
            listView.adapter = detailsAdapter
            detailsAdapter.notifyDataSetChanged()
        }
    }
}
