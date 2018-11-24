package com.tilly.steven.stlbusarrivals;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.wearable.DataMap;

/**
 * Created by Steven on 2016-02-06.
 */
public class Point {

    double latitude, longitude;

    public Point(DataMap map){
        longitude = map.getDouble("longitude");
        latitude = map.getDouble("latitude");
    }

    /*public Point(String object){
        if (null != object && object.length() > 0 )
        {
            String[] separated = object.split("[+]");
            latitude = separated[1];
            longitude = separated[2];
        }
    }*/

    public LatLng getLatLng(){
        return new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
    }

    /*public void setData(DataMap map) {
        map.getDouble("longitude", longitude);
        map.getDouble("latitude", latitude);
    }*/
}
