package com.example.steven.stlbusarrivals;

import android.util.Log;

import com.example.steven.stlbusarrivals.dao2.DatabaseHelper;
import com.example.steven.stlbusarrivals.model2.Details;
import com.example.steven.stlbusarrivals.model2.PathBounds;
import com.example.steven.stlbusarrivals.model2.PathList;
import com.example.steven.stlbusarrivals.model2.TimeList;
import com.example.steven.stlbusarrivals.model2.VehiculeList;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Steven on 2016-02-02.
 */
public class WearService extends WearableListenerService  implements Observer {

    GoogleApiClient googleClient;
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
            //Send the location request
            String url1 = "http://webservices.nextbus.com/service/publicXMLFeed?command=vehicleLocations&a=stl&r=" + routeTag + "&t=0";
            RequestSender locationRequest = new RequestSender(this,Constants.LOCATION_XML,url1);
            locationRequest.addObserver(this);
            locationRequest.sendRequest();
            //send the stop request
            String url2 = "http://webservices.nextbus.com/service/publicXMLFeed?command=routeConfig&a=stl&r="+ routeTag;
            RequestSender stopRequest =new RequestSender(this,Constants.STOP_XML,url2);
            stopRequest.addObserver(this);
            stopRequest.sendRequest();
        }else {
            super.onMessageReceived(messageEvent);
        }
    }

    public void getDataList(){
        detailsList = getHelper().getAllOrderedDetails();
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