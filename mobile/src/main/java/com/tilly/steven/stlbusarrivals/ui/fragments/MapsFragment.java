package com.tilly.steven.stlbusarrivals.ui.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tilly.steven.stlbusarrivals.Model.PathBounds;
import com.tilly.steven.stlbusarrivals.Model.PathList;
import com.tilly.steven.stlbusarrivals.Model.Point;
import com.tilly.steven.stlbusarrivals.Model.VehiculeList;
import com.tilly.steven.stlbusarrivals.R;
import com.tilly.steven.stlbusarrivals.VolleySingleton;
import com.tilly.steven.stlbusarrivals.XmlParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Steven on 2016-01-28.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback, Observer {

    public MapsFragment() {
    }

    private GoogleMap mMap;
    private ArrayList<ArrayList<Point>> pathList;
    private VehiculeList vehiculeList;
    private XmlParser xmlparser = new XmlParser();
    private String longitude, latitude, routeTag;
    private PathBounds pathBounds;
    private Handler mHandler;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        xmlparser.addObserver(this);
        routeTag = getArguments().getString("routeTag");
        mHandler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_map, container, false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().
                findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mStatusChecker);
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            sendDetailRequest();
            int mInterval = 10000;
            mHandler.postDelayed(mStatusChecker, mInterval);
        }
    };

    private void sendPathRequest() {
        RequestQueue queue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        xmlparser.addObserver(this);
        String url = "http://webservices.nextbus.com/service/publicXMLFeed?command=routeConfig&a=stl&r=" + routeTag;
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // we got the response, now our job is to handle it
                //parseXmlResponse(response);
                try {
                    xmlparser.readStopXml(response);
                } catch(Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                sendPathRequest();
            }
        });
        queue.add(request);
    }

    private void sendDetailRequest() {
        RequestQueue queue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        String url = "http://webservices.nextbus.com/service/publicXMLFeed?command=vehicleLocations&a=stl&r=" + routeTag + "&t=0";
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // we got the response, now our job is to handle it
                //parseXmlResponse(response);
                try {
                    xmlparser.readLocation(response);
                } catch(Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                sendDetailRequest();
            }
        });
        queue.add(request);
    }

    @Override
    public void update(Observable observable, Object o) {
        if(getActivity() != null) {
            if(o instanceof VehiculeList) {
                vehiculeList = (VehiculeList) o;
                if(vehiculeList.size() != 0) {
                    mMap.clear();
                    for(int i = 0; i < vehiculeList.size(); i++) {
                        longitude = vehiculeList.get(i).getLongitude();
                        latitude = vehiculeList.get(i).getLatitude();
                        if(!longitude.equals("false")) {
                            LatLng bus = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
                            mMap.addMarker(new MarkerOptions().position(bus).title(getString(R.string.bus_location))
                                    .icon(BitmapDescriptorFactory.fromBitmap(mapIcon()))
                                    .anchor(0.5f, 0.5f));
                        }
                    }
                } else {
                    mMap.clear();
                    Toast.makeText(getActivity(), getString(R.string.server_no_bus), Toast.LENGTH_SHORT).show();
                }
                for(int j = pathList.size() - 1; j >= 0; j--) {
                    for(int i = pathList.get(j).size() - 1; i > 0; i--) {

                        mMap.addPolyline(new PolylineOptions()
                                .add(pathList.get(j).get(i).getLatLng(), pathList.get(j).get(i - 1).getLatLng())
                                .width(5)
                                .color(Color.BLUE));
                    }
                }
            }
            if(o instanceof PathList) {
                pathList = (ArrayList<ArrayList<Point>>) o;
                startRepeatingTask();
            }
            if(o instanceof PathBounds) {
                pathBounds = (PathBounds) o;
                LatLngBounds bounds = new LatLngBounds(
                        pathBounds.getMinBounds(), pathBounds.getMaxBounds());
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 15));
            }
        }
    }

    private Bitmap mapIcon() {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.map_marker);
        return Bitmap.createScaledBitmap(imageBitmap, 64, 64, false);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        sendPathRequest();

    }
}
