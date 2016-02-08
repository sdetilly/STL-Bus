package com.example.steven.stlbusarrivals;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends WearableActivity {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    GoogleApiClient googleClient;

    private BoxInsetLayout mContainerView;
    private TextView mClockView;
    private ListView listView;
    private DetailsAdapter detailsAdapter;
    private ArrayList<Details> detailsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

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
                new SendToDataLayerThread("/maps_req", routeTag).start();

                Intent stopSearchIntent = new Intent(getBaseContext(), MapsActivity.class);
                //stopSearchIntent.putExtra("stopId", stopId);
                //stopSearchIntent.putExtra("routeTag", routeTag);
                //stopSearchIntent.putExtra("routeName", routeName);
                //stopSearchIntent.putExtra("stopName", stopName);
                startActivity(stopSearchIntent);
            }
        });

        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        googleClient.connect();
        new SendToDataLayerThread("/details_req", "message").start();
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);
    }

    @Override
    public void onResume(){
        super.onResume();
        mClockView.setVisibility(View.VISIBLE);
        mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            String[] separated = message.split("[+]");
            if(separated[0].equals("details")) {
                Log.v("wearMainAct", "Main activity received message: " + message);
                Details details = new Details(message);
                detailsList.add(details);
                listView.setAdapter(new DetailsAdapter(getBaseContext(), R.layout.row_favorites, detailsList));
                // Display message in UI
            }
        }
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

    class SendToDataLayerThread extends Thread {
        String path;
        String message;

        // Constructor to send a message to the data layer
        SendToDataLayerThread(String p, String msg) {
            path = p;
            message = msg;
        }

        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
            Log.v("wearThread", "inside SendData ");
            for (Node node : nodes.getNodes()) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient, node.getId(), path, message.getBytes()).await();
                if (result.getStatus().isSuccess()) {
                    Log.v("wearThread", "Message: {" + message + "} sent to: " + node.getDisplayName());
                } else {
                    // Log an error
                    Log.v("wearThread", "ERROR: failed to send Message");
                }
            }
        }
    }
}
