package com.example.steven.stlbusarrivals.UI.Fragments;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.steven.stlbusarrivals.Model.TimeList;
import com.example.steven.stlbusarrivals.Model.VehiculeList;
import com.example.steven.stlbusarrivals.R;
import com.example.steven.stlbusarrivals.UI.Adapter.DetailsAdapter;
import com.example.steven.stlbusarrivals.VolleySingleton;
import com.example.steven.stlbusarrivals.XmlParser;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Steven on 2016-01-28.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback, Observer {

    public MapsFragment(){}

    private GoogleMap mMap;
    private static VehiculeList vehiculeList;
    private XmlParser xmlparser = new XmlParser();
    private String longitude, latitude, routeTag;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        xmlparser.addObserver(this);
        routeTag = getArguments().getString("routeTag");
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
    public void onResume(){
        super.onResume();
        sendRequest();
    }

    private void sendRequest(){
        RequestQueue queue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        String url = "http://webservices.nextbus.com/service/publicXMLFeed?command=vehicleLocations&a=stl&r=" + routeTag + "&t=0";
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // we got the response, now our job is to handle it
                //parseXmlResponse(response);
                try{
                    xmlparser.readLocation(response);
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                sendRequest();
            }
        });
        queue.add(request);
    }

    @Override
    public void update(Observable observable, Object o) {
        Log.d("mapfrag update", "entered");
        if(o instanceof VehiculeList) {
            vehiculeList = (VehiculeList) o;
            if (vehiculeList.size() != 0) {
                longitude = vehiculeList.get(0).getLongitude();
                latitude = vehiculeList.get(0).getLatitude();
                if(longitude != "false"){
                mMap.clear();
                LatLng bus = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
                CameraUpdate center =
                        CameraUpdateFactory.newLatLng(bus);
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);

                mMap.moveCamera(center);
                mMap.addMarker(new MarkerOptions().position(bus).title("Bus location"));
                mMap.animateCamera(zoom);
                }
            }else{
                latitude= "45.5833";
                longitude = "-73.7500";
                mMap.clear();
                LatLng bus = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
                CameraUpdate center =
                        CameraUpdateFactory.newLatLng(bus);
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);

                mMap.moveCamera(center);
                mMap.animateCamera(zoom);
                Toast.makeText(getActivity(), "server did not return vehicle location", Toast.LENGTH_SHORT).show();
                /*LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                longitude = String.valueOf(location.getLongitude());
                latitude = String.valueOf(location.getLatitude());*/

            }

        }
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

        mMap.setMyLocationEnabled(true);

    }
}
