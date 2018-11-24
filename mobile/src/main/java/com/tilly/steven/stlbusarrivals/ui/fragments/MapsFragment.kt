package com.tilly.steven.stlbusarrivals.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.tilly.steven.stlbusarrivals.R
import com.tilly.steven.stlbusarrivals.Utils
import com.tilly.steven.stlbusarrivals.VolleySingleton
import com.tilly.steven.stlbusarrivals.XmlParser
import com.tilly.steven.stlbusarrivals.model.PathBounds
import com.tilly.steven.stlbusarrivals.model.PathList
import com.tilly.steven.stlbusarrivals.model.Point
import com.tilly.steven.stlbusarrivals.model.VehiculeList
import java.util.*

/**
 * Created by Steven on 2016-01-28.
 */
class MapsFragment : Fragment(), OnMapReadyCallback, Observer {

    private var mMap: GoogleMap? = null
    private var pathList: ArrayList<ArrayList<Point>>? = null
    private var vehiculeList: VehiculeList? = null
    private val xmlparser = XmlParser()
    private var longitude: String? = null
    private var latitude: String? = null
    private var routeTag: String? = null
    private var pathBounds: PathBounds? = null
    private lateinit var mHandler: Handler

    private var mStatusChecker: Runnable = object : Runnable {
        override fun run() {
            sendDetailRequest()
            val mInterval = 10000
            mHandler.postDelayed(this, mInterval.toLong())
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        xmlparser.addObserver(this)
        routeTag = arguments!!.getString("routeTag")
        mHandler = Handler()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_map, container, false) as ViewGroup
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        return v
    }

    private fun startRepeatingTask() {
        mStatusChecker.run()
    }

    override fun onStop() {
        super.onStop()
        mHandler.removeCallbacks(mStatusChecker)
    }

    private fun sendPathRequest() {
        val queue = VolleySingleton.getInstance(activity!!).requestQueue
        xmlparser.addObserver(this)
        val url = "http://webservices.nextbus.com/service/publicXMLFeed?command=routeConfig&a=stl&r=" + routeTag!!
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
            sendPathRequest()
        })
        queue.add(request)
    }

    private fun sendDetailRequest() {
        val queue = VolleySingleton.getInstance(activity!!).requestQueue
        val url = "http://webservices.nextbus.com/service/publicXMLFeed?command=vehicleLocations&a=stl&r=$routeTag&t=0"
        val request = StringRequest(url, Response.Listener { response ->
            // we got the response, now our job is to handle it
            //parseXmlResponse(response);
            try {
                xmlparser.readLocation(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { error ->
            error.printStackTrace()
            sendDetailRequest()
        })
        queue.add(request)
    }

    override fun update(observable: Observable, o: Any) {
        if (activity != null) {
            if (o is VehiculeList) {
                vehiculeList = o
                if (vehiculeList!!.size != 0) {
                    mMap!!.clear()
                    for (i in vehiculeList!!.indices) {
                        longitude = vehiculeList!![i].longitude
                        latitude = vehiculeList!![i].latitude
                        if (longitude != "false") {
                            val bus = LatLng(java.lang.Double.valueOf(latitude!!), java.lang.Double.valueOf(longitude!!))
                            mMap!!.addMarker(MarkerOptions().position(bus).title(getString(R.string.bus_location))
                                    .icon(BitmapDescriptorFactory.fromBitmap(mapIcon()))
                                    .anchor(0.5f, 0.5f))
                        }
                    }
                } else {
                    mMap!!.clear()
                    Toast.makeText(activity, getString(R.string.server_no_bus), Toast.LENGTH_SHORT).show()
                }
                for (j in pathList!!.indices.reversed()) {
                    for (i in pathList!![j].size - 1 downTo 1) {

                        mMap!!.addPolyline(PolylineOptions()
                                .add(pathList!![j][i].latLng, pathList!![j][i - 1].latLng)
                                .width(5f)
                                .color(Color.BLUE))
                    }
                }
            }
            if (o is PathList) {
                pathList = o as ArrayList<ArrayList<Point>>
                startRepeatingTask()
            }
            if (o is PathBounds) {
                pathBounds = o
                val bounds = LatLngBounds(
                        pathBounds!!.minBounds, pathBounds!!.maxBounds)
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 15))
            }
        }
    }

    private fun mapIcon(): Bitmap {
        val imageBitmap = BitmapFactory.decodeResource(resources, R.mipmap.map_marker)
        return Bitmap.createScaledBitmap(imageBitmap, 64, 64, false)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            val permissionCheck = ContextCompat.checkSelfPermission(
                    context!!, Manifest.permission.ACCESS_FINE_LOCATION)
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                                Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Utils.toast(context!!, "Permission Needed")
                } else {
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
                }
            } else {
                Toast.makeText(context, "Permission (already) Granted!", Toast.LENGTH_SHORT).show()
            }
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mMap!!.isMyLocationEnabled = true
        sendPathRequest()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            100 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return
                }
                mMap!!.isMyLocationEnabled = true
                sendPathRequest()
                Toast.makeText(context, "Permission Granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
