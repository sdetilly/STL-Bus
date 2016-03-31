package com.example.steven.stlbusarrivals.ui.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.steven.stlbusarrivals.Constants;
import com.example.steven.stlbusarrivals.model.PathBounds;
import com.example.steven.stlbusarrivals.model.PathList;
import com.example.steven.stlbusarrivals.model.Point;
import com.example.steven.stlbusarrivals.model.VehiculeList;
import com.example.steven.stlbusarrivals.R;
import com.example.steven.stlbusarrivals.RequestSender;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

    public MapsFragment(){}

    private GoogleMap mMap;
    private ArrayList<ArrayList<Point>> pathList;
    private String routeTag;
    private PathBounds pathBounds;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        String url = "http://webservices.nextbus.com/service/publicXMLFeed?command=routeConfig&a=stl&r="+ routeTag;
        RequestSender stopRequest = new RequestSender(getActivity(),Constants.STOP_XML, url);
        stopRequest.addObserver(this);
        stopRequest.sendRequest();
    }

    @Override
    public void update(Observable observable, Object o) {
        if(o instanceof VehiculeList) {
            VehiculeList vehiculeList = (VehiculeList) o;
            LatLngBounds bounds = new LatLngBounds(
                    pathBounds.getMinBounds(), pathBounds.getMaxBounds());
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 15));
            if (vehiculeList.size() != 0) {
                String longitude = vehiculeList.get(0).getLongitude();
                String latitude = vehiculeList.get(0).getLatitude();
                if(!longitude.equals("false")){
                mMap.clear();
                LatLng bus = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
                mMap.addMarker(new MarkerOptions().position(bus).title("Bus location"));
                }
            }else{
                mMap.clear();
                Toast.makeText(getActivity(), "server did not return vehicle location", Toast.LENGTH_SHORT).show();
            }
            for(int j=pathList.size()-1; j>=0; j--) {
                for (int i = pathList.get(j).size() - 1; i > 0; i--) {

                    mMap.addPolyline(new PolylineOptions()
                            .add(pathList.get(j).get(i).getLatLng(), pathList.get(j).get(i - 1).getLatLng())
                            .width(5)
                            .color(Color.BLUE));
                }
            }
        }
        if(o instanceof PathList){
            pathList = (ArrayList<ArrayList<Point>>) o;
            String url = "http://webservices.nextbus.com/service/publicXMLFeed?command=vehicleLocations&a=stl&r=" + routeTag + "&t=0";
            RequestSender locationRequest = new RequestSender(getActivity(),Constants.LOCATION_XML, url);
            locationRequest.addObserver(this);
            locationRequest.sendRequest();
        }
        if(o instanceof PathBounds){
            pathBounds = (PathBounds) o;
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
