package com.example.steven.stlbusarrivals.Model;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.steven.stlbusarrivals.VolleySingleton;
import com.example.steven.stlbusarrivals.XmlParser;
import com.j256.ormlite.field.DatabaseField;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Steven on 2016-01-29.
 */
public class Details extends Observable implements Observer{

    private XmlParser xmlparser = new XmlParser();
    private Context context;
    String prediction;
    private TimeList timeList;

    @DatabaseField(generatedId = true)
    int id;
    @DatabaseField
    String routeTag;
    @DatabaseField
    String stopId;
    @DatabaseField
    String routeName;
    @DatabaseField
    String stopName;


    public Details(){}

    public Details(Context context){this.context = context;}

    public int getId(){return id;}

    public String getTag(){return routeTag;}

    public void setTag(String routeTag){this.routeTag = routeTag;}

    public String getStopId(){return stopId;}

    public void setStopId(String stopId){this.stopId = stopId;}

    public String getRouteName(){return routeName;}

    public void setRouteName(String routeName){this.routeName = routeName;}

    public String getStopName(){
        String[] separated = stopName.split(" ",2);
        return stopName;}

    public void setStopName(String stopName){this.stopName = stopName;}

    public void setPrediction(String prediction){this.prediction = prediction;}

    public String getPrediction(){return prediction;}

    public void setPredictionNull(){prediction = null;}

    public String sendToWearable(){
        String[] separated = stopName.split(" ",2);
        return "details+" + routeName + "+" + stopName + "+" + prediction + "+" + routeTag;
    }

    public void getNetPrediction(){
        xmlparser.addObserver(this);
        RequestQueue queue = VolleySingleton.getInstance(context).getRequestQueue();
        String url = "http://webservices.nextbus.com/service/publicXMLFeed?command=predictions&a=stl&stopId=" + stopId + "&routeTag=" + routeTag;
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // we got the response, now our job is to handle it
                //parseXmlResponse(response);
                try{
                    xmlparser.readPrediction(response);
                    Log.d("unitDetails", "This detail is parsing...");
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                getNetPrediction();
            }
        });
        queue.add(request);
    }

    @Override
    public void update(Observable observable, Object o) {
        Log.d("unitDetails", "This detail has been updated");
        if(o instanceof TimeList){
            timeList = (TimeList) o;
            if(timeList.size() > 0) {
                this.setPrediction(timeList.get(0).getTime());

            }
            setChanged();
            notifyObservers(timeList);
            Log.d("unitDetails", "This detail's prediction is : "+ this.getPrediction());
        }
    }
}
