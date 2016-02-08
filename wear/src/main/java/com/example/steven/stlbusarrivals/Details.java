package com.example.steven.stlbusarrivals;

/**
 * Created by Steven on 2016-02-05.
 */
public class Details {

    String routeName, stopName, prediction, routeTag;

    public Details(String object){
        if (null != object && object.length() > 0 )
        {
                String[] separated = object.split("[+]");
                routeName = separated[1];
                stopName = separated[2];
                prediction = separated[3];
                routeTag = separated[4];
        }
    }

    public String getRouteName(){return routeName;}

    public String getStopName() {
        return stopName;
    }

    public String getRouteTag() {
        return routeTag;
    }

    public String getPrediction(){return prediction;}

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }
}
