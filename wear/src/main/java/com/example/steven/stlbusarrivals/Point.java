package com.example.steven.stlbusarrivals;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Steven on 2016-02-06.
 */
public class Point {

    String latitude, longitude;

    public Point(String object){
        if (null != object && object.length() > 0 )
        {
            String[] separated = object.split("[+]");
            latitude = separated[1];
            longitude = separated[2];
        }
    }

    public LatLng getLatLng(){
        return new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
    }
}
