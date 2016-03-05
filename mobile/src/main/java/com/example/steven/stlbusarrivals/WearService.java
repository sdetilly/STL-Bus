package com.example.steven.stlbusarrivals;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.steven.stlbusarrivals.Dao.DatabaseHelper;
import com.example.steven.stlbusarrivals.Model.Details;
import com.example.steven.stlbusarrivals.Model.DetailsList;
import com.example.steven.stlbusarrivals.Model.PathBounds;
import com.example.steven.stlbusarrivals.Model.PathList;
import com.example.steven.stlbusarrivals.Model.TimeList;
import com.example.steven.stlbusarrivals.Model.Vehicule;
import com.example.steven.stlbusarrivals.Model.VehiculeList;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import org.joda.time.DateTime;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Steven on 2016-02-02.
 */
public class WearService extends WearableListenerService  implements Observer {

    GoogleApiClient googleClient;
    XmlParser xmlparser = new XmlParser();
    ArrayList<Details> detailsList;
    DatabaseHelper databaseHelper = null;
    static String routeTag;
    static int update= 0; //to send the data only when all details have finished syncing

    @Override
    public void onCreate(){
        super.onCreate();
        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        googleClient.connect();
    }
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.v("mobListenerService", "Message path received on phone is: " + messageEvent.getPath());

        if (messageEvent.getPath().equals("/details_req")) {
            final String message = new String(messageEvent.getData());

            Log.v("mobListenerService", "Message received on phone is: " + message);
            getDataList();
        }else if (messageEvent.getPath().equals("/maps_req")) {
            routeTag = new String(messageEvent.getData());

            Log.v("mobListenerService", "RouteTag received on phone is: " + routeTag);

            // Broadcast message to wearable activity for display
            sendDetailRequest();
            sendPathRequest();
        }else {
            super.onMessageReceived(messageEvent);
        }
    }

    private void sendDetailRequest(){
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        String url = "http://webservices.nextbus.com/service/publicXMLFeed?command=vehicleLocations&a=stl&r=" + routeTag + "&t=0";
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // we got the response, now our job is to handle it
                //parseXmlResponse(response);
                try{
                    xmlparser.readLocation(response);
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                sendDetailRequest();
            }
        });
        queue.add(request);
    }

    private void sendPathRequest(){
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        xmlparser.addObserver(this);
        String url = "http://webservices.nextbus.com/service/publicXMLFeed?command=routeConfig&a=stl&r="+ routeTag;
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // we got the response, now our job is to handle it
                //parseXmlResponse(response);
                try{
                    xmlparser.readStopXml(response);
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                sendPathRequest();
            }
        });
        queue.add(request);
    }

    public void getDataList(){
        detailsList = getAllOrderedDetails();
        for(int i=0; i<detailsList.size(); i++){
            detailsList.get(i).addObserver(this);
            detailsList.get(i).getNetPrediction(this);
        }
    }


    class SendToDataLayerThread extends Thread {
        String path;
        DataMap map;

        // Constructor to send a message to the data layer
        SendToDataLayerThread(String p, DataMap map) {
            path = p;
            this.map = map;
        }

        public void run() {
            DateTime dateTime = new DateTime();
            PutDataMapRequest dataMapReq = PutDataMapRequest.create(path);
            DataMap dataMap = dataMapReq.getDataMap();

            dataMap.putDataMap("map", map);
            dataMap.putString("time",String.valueOf(dateTime.getMillisOfSecond()));

            PutDataRequest request = dataMapReq.asPutDataRequest();
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
            for (Node node : nodes.getNodes()) {
                DataApi.DataItemResult dataItemResult = Wearable.DataApi
                        .putDataItem(googleClient, request).await();
                if (dataItemResult.getStatus().isSuccess()) {
                    Log.v("mobSendData", "Data sent to: " + node.getDisplayName());
                }else {
                    // Log an error
                    Log.v("mobSendData", "ERROR: failed to send Message");
                }
            }
        }
    }

    private DatabaseHelper getHelper(){
        if(databaseHelper == null){
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    private ArrayList<Details> getAllOrderedDetails() {
        // Construct the data source
        // get our query builder from the DAO
        QueryBuilder<Details, Integer> queryBuilder = getHelper().getDetailsDao().queryBuilder();
        // the 'password' field must be equal to "qwerty"
        // prepare our sql statement
        PreparedQuery<Details> preparedQuery = null;
        try {
            preparedQuery = queryBuilder.prepare();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return (ArrayList) getHelper().getDetailsDao().query(preparedQuery);
    }

    @Override
    public void onDestroy() {
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        if(databaseHelper!=null){
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
        super.onDestroy();
    }

    @Override
    public void update(Observable observable, Object o){
        if(o instanceof TimeList) {
            update++;
            if (update == detailsList.size()) {
                DataMap detailMap = new DataMap();
                for (int i = 0; i < detailsList.size(); i++) {
                    detailMap.putDataMap("detail"+i,detailsList.get(i).putData());
                }
                update = 0;
                new SendToDataLayerThread("/details_send", detailMap).start();
            }
        }
        if(o instanceof VehiculeList){
            VehiculeList vehiculeList = (VehiculeList) o;
            if(vehiculeList.size() > 0) {
                DataMap vehiculeMap = new DataMap();
                vehiculeMap.putDataMap("vehicule", vehiculeList.get(0).putData());
                new SendToDataLayerThread("/maps_vehicule", vehiculeMap).start();
            }
        }
        if(o instanceof PathBounds){
            PathBounds pathBounds = (PathBounds) o;
            DataMap pathMap = new DataMap();
            pathMap.putDataMap("pathBounds",pathBounds.putData());
            new SendToDataLayerThread("/maps_pathBounds", pathMap).start();
        }
        if(o instanceof PathList) {
            PathList pathList = (PathList) o;
            DataMap pathMap = new DataMap();
            pathMap.putDataMap("pathList", pathList.putData());
            new SendToDataLayerThread("/maps_pathList", pathMap).start();
        }
    }
}