package com.example.steven.stlbusarrivals;

import com.google.android.gms.wearable.DataMap;

/**
 * Created by Steven on 2016-02-05.
 */
public class Details {

    String routeName, stopName, prediction, routeTag;

    public Details(){}

    public void setData(DataMap map){
        routeName = map.getString("routeName");
        stopName = map.getString("stopName");
        prediction = map.getString("prediction");
        routeTag = map.getString("routeTag");
    }

    public String getRouteName(){return routeName;}

    public String getStopName() {
        return stopName;
    }

    public String getRouteTag() {
        return routeTag;
    }

    public String getPrediction(){return prediction;}

}
