package com.example.steven.stlbusarrivals;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.steven.stlbusarrivals.model2.PathBounds;
import com.example.steven.stlbusarrivals.model2.PathList;
import com.example.steven.stlbusarrivals.model2.RouteList;
import com.example.steven.stlbusarrivals.model2.StopList;
import com.example.steven.stlbusarrivals.model2.TimeList;
import com.example.steven.stlbusarrivals.model2.VehiculeList;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Steven on 2016-03-06.
 */
public class RequestSender extends Observable implements Observer {
    String url;
    int type;
    Context context;
    XmlParser xmlparser = new XmlParser();

    public RequestSender(Context ctx, int type, String url){
        context = ctx;
        this.type = type;
        this.url = url;
    }

    public void sendRequest(){
        xmlparser.addObserver(this);
        RequestQueue queue = VolleySingleton.getInstance(context).getRequestQueue();
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // we got the response, now our job is to handle it
                //parseXmlResponse(response);
                try{
                    //xmlparser.readRouteXml(response);
                    xmlparser.readXml(type, response);
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                sendRequest();
            }
        });
        queue.add(request);
    }

    @Override
    public void update(Observable observable, Object data) {
        setChanged();
        ArrayList list;
        if(data instanceof ArrayList) {
            if (data instanceof TimeList) {
                list = (TimeList) data;
            } else if (data instanceof VehiculeList) {
                list = (VehiculeList) data;
            } else if (data instanceof RouteList) {
                list = (RouteList) data;
            } else if (data instanceof PathList) {
                list = (PathList) data;
            } else if (data instanceof StopList) {
                list = (StopList) data;
            }else {
                list = new ArrayList();
            }
            notifyObservers(list);
        }
        if (data instanceof PathBounds) {
            PathBounds bounds = (PathBounds) data;
            notifyObservers(bounds);
        }
    }
}
