package com.example.steven.stlbusarrivals.model2;

import com.google.android.gms.wearable.DataMap;

/**
 * Created by Steven on 2016-01-28.
 */
public class Vehicule {

    String longitude;

    String latitude;

    public Vehicule(){}

    public DataMap putData(){
        DataMap map = new DataMap();
        map.putString("longitude", longitude);
        map.putString("latitude", latitude);
        return map;
    }

    public String getLongitude(){return longitude;}

    public String getLatitude(){return latitude;}

    public void setLongitude(String longitude){this.longitude = longitude;}

    public void setLatitude(String latitude){this.latitude = latitude;}

    public String sendToWearable(){
        return "vehicule+" + latitude + "+" + longitude;
    }
}
