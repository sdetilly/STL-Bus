package com.example.steven.stlbusarrivals;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.DismissOverlayView;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

public class MapsActivity extends Activity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener {

    /**
     * Overlay that shows a short help text when first launched. It also provides an option to
     * exit the app.
     */
    private DismissOverlayView mDismissOverlay;

    private GoogleMap mMap;

    private String longitude, latitude;

    private static Path path = new Path();
    private static PathList pathList = new PathList();

    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        // Set the layout. It only contains a MapFragment and a DismissOverlay.
        setContentView(R.layout.activity_maps);

        // Retrieve the containers for the root of the layout and the map. Margins will need to be
        // set on them to account for the system window insets.
        final FrameLayout topFrameLayout = (FrameLayout) findViewById(R.id.root_container);
        final FrameLayout mapFrameLayout = (FrameLayout) findViewById(R.id.map_container);

        // Set the system view insets on the containers when they become available.
        topFrameLayout.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                // Call through to super implementation and apply insets
                insets = topFrameLayout.onApplyWindowInsets(insets);

                FrameLayout.LayoutParams params =
                        (FrameLayout.LayoutParams) mapFrameLayout.getLayoutParams();

                // Add Wearable insets to FrameLayout container holding map as margins
                params.setMargins(
                        insets.getSystemWindowInsetLeft(),
                        insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(),
                        insets.getSystemWindowInsetBottom());
                mapFrameLayout.setLayoutParams(params);

                return insets;
            }
        });

        // Obtain the DismissOverlayView and display the introductory help text.
        mDismissOverlay = (DismissOverlayView) findViewById(R.id.dismiss_overlay);
        mDismissOverlay.setIntroText(R.string.intro_text);
        mDismissOverlay.showIntroIfNecessary();

        // Obtain the MapFragment and set the async listener to be notified when the map is ready.
        MapFragment mapFragment =
                (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String message = intent.getStringExtra("message");
            final String[] separated = message.split("[+]");
            if(separated[0].equals("vehicule")) {
                latitude = separated[1];
                longitude = separated[2];

                    if(!longitude.equals("false")){
                        mMap.clear();
                        LatLng bus = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
                        mMap.addMarker(new MarkerOptions().position(bus).title("Bus location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(bus));

                }else{
                    mMap.clear();
                    Toast.makeText(MapsActivity.this, "server did not return vehicle location", Toast.LENGTH_SHORT).show();
                }
            }
            if(separated[0].equals("pathbounds")){
                LatLngBounds bounds = new LatLngBounds(
                        new LatLng(Double.valueOf(separated[1]), Double.valueOf(separated[2])),
                        new LatLng(Double.valueOf(separated[3]), Double.valueOf(separated[4])));
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 15));
            }
            if(separated[0].equals("point")){
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        if(!separated[1].equals("end")) {
                            Point point = new Point(message);
                            path.add(point);
                        }
                        if(separated[1].equals("end")){
                            pathList.add(path);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        for(int j=pathList.size()-1; j>=0; j--) {
                            for (int i = pathList.get(j).size() - 1; i > 0; i--) {

                                mMap.addPolyline(new PolylineOptions()
                                        .add(pathList.get(j).get(i).getLatLng(), pathList.get(j).get(i - 1).getLatLng())
                                        .width(5)
                                        .color(Color.BLUE));
                            }
                        }
                    }
                }.execute();

            }
            /*if(separated[0].equals("path")){
            }*/
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Map is ready to be used.
        mMap = googleMap;
        // Set the long click listener as a way to exit the map.
        mMap.setOnMapLongClickListener(this);
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        // Display the dismiss overlay with a button to exit this activity.
        mDismissOverlay.show();
    }
}
