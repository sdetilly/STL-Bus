package com.example.steven.stlbusarrivals.Model;

/**
 * Created by Steven on 2016-01-28.
 */
public class Vehicule {

    String longitude;

    String latitude;

    public Vehicule(){}

    public String getLongitude(){return longitude;}

    public String getLatitude(){return latitude;}

    public void setLongitude(String longitude){this.longitude = longitude;}

    public void setLatitude(String latitude){this.latitude = latitude;}

    public String sendToWearable(){
        return "vehicule+" + latitude + "+" + longitude;
    }
}
