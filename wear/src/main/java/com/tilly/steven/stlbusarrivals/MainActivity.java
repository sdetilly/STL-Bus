package com.tilly.steven.stlbusarrivals;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends WearableActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener, MessageApi.MessageListener{

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    GoogleApiClient googleClient;

    private BoxInsetLayout mContainerView;
    private TextView mClockView;
    private ListView listView;
    private DetailsAdapter detailsAdapter;
    private ArrayList<Details> detailsList;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        mHandler = new Handler();
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mClockView = (TextView) findViewById(R.id.clock);
        listView = (ListView) findViewById(R.id.list);
        detailsList = new ArrayList<Details>();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Details item = detailsList.get(position);
                //String stopId = item.getStopId();
                String stopName = item.getStopName();
                String routeTag = item.getRouteTag();
                String routeName = item.getRouteName();

                Intent stopSearchIntent = new Intent(getBaseContext(), MapsActivity.class);
                //stopSearchIntent.putExtra("stopId", stopId);
                stopSearchIntent.putExtra("routeTag", routeTag);
                //stopSearchIntent.putExtra("routeName", routeName);
                //stopSearchIntent.putExtra("stopName", stopName);
                startActivity(stopSearchIntent);
            }
        });

        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleClient.connect();

    }

    @Override
    public void onResume(){
        super.onResume();
        mClockView.setVisibility(View.VISIBLE);
        mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
    }

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mStatusChecker);
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            new SendToDataLayerThread(googleClient,"/details_req", "message").start();
            int mInterval = 60000;
            mHandler.postDelayed(mStatusChecker, mInterval);
        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(googleClient, this);
        Wearable.MessageApi.addListener(googleClient, this);
        startRepeatingTask();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        Log.v("onDataChanged", "entered successfully" );
        for (DataEvent event : dataEvents)
        {
            Log.v("onDataChanged", "entered for with " + event.getDataItem().getUri().getPath() );
            if(event.getDataItem().getUri().getPath().equals("/details_send")) {
                DataItem item = event.getDataItem();
                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                if (!dataMap.get("map").equals("")) {
                    DataMap mMap = dataMap.getDataMap("map");
                    detailsList.clear();
                    for (int i = 0; i < mMap.size(); i++) {
                        Details details = new Details();
                        details.setData(mMap.getDataMap("detail" + i));
                        detailsList.add(details);
                    }
                    listView.setAdapter(new DetailsAdapter(getBaseContext(), R.layout.row_favorites, detailsList));
                }
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        mClockView.setVisibility(View.VISIBLE);
        mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
    }


}
