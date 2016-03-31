package com.example.steven.stlbusarrivals.model2;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.wearable.DataMap;

/**
 * Created by Steven on 2016-02-04.
 */
public class PathBounds {

    double latMin, latMax, longMin, longMax;

    public PathBounds(){}

    public DataMap putData(){
        DataMap map = new DataMap();
        map.putDouble("latMin", latMin);
        map.putDouble("latMax", latMax);
        map.putDouble("longMin", longMin);
        map.putDouble("longMax", longMax);
        return map;
    }

    public LatLng getMinBounds(){
        return new LatLng(latMin, longMin);
    }

    public LatLng getMaxBounds(){
        return new LatLng(latMax, longMax);
    }

    public String sendToWearable(){
        return "pathbounds+" + latMin + "+" + longMin + "+" + latMax + "+" + longMax;
    }

    public void setLatMax(double latMax) {
        this.latMax = latMax;
    }

    public void setLatMin(double latMin) {
        this.latMin = latMin;
    }

    public void setLongMax(double longMax) {
        this.longMax = longMax;
    }

    public void setLongMin(double longMin) {
        this.longMin = longMin;
    }
}
