package com.example.steven.stlbusarrivals;

import com.google.android.gms.wearable.DataMap;

/**
 * Created by Steven on 2016-02-26.
 */
public class Vehicule {

    String longitude;

    String latitude;

    public Vehicule(){}

    public void setData(DataMap map){
        longitude = map.getString("longitude");
        latitude = map.getString("latitude");
    }

    public String getLongitude(){return longitude;}

    public String getLatitude(){return latitude;}
}
