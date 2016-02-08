package com.example.steven.stlbusarrivals.Model;

import com.google.android.gms.maps.model.LatLng;
import java.io.Serializable;

/**
 * Created by Steven on 2016-02-03.
 */
public class Point implements Serializable {

    double longitude;
    double latitude;

    public Point(){}

    public void setLongitude(double longitude){this.longitude = longitude;}

    public void setLatitude(double latitude){this.latitude = latitude;}

    public LatLng getLatLng(){
        return new LatLng(latitude,longitude);
    }

    public String sendToWearable(){
        return "point+" + latitude + "+" + longitude;
    }
}
