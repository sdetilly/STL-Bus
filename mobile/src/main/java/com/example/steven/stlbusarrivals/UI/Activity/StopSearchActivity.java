package com.example.steven.stlbusarrivals.UI.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.steven.stlbusarrivals.Model.PathList;
import com.example.steven.stlbusarrivals.Model.Route;
import com.example.steven.stlbusarrivals.Model.Stop;
import com.example.steven.stlbusarrivals.Model.StopList;
import com.example.steven.stlbusarrivals.R;
import com.example.steven.stlbusarrivals.UI.Adapter.StopSearchAdapter;
import com.example.steven.stlbusarrivals.VolleySingleton;
import com.example.steven.stlbusarrivals.XmlParser;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Steven on 2016-01-28.
 */
public class StopSearchActivity extends AppCompatActivity implements Observer {
    private static String routeTag, routeName;

    private ListView listView;
    private static StopList stopList;
    private XmlParser xmlparser = new XmlParser();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_search);
        listView = (ListView) findViewById(R.id.list_stop_search);
        if(savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            routeTag = extras.getString("tag");
            routeName = extras.getString("routeName");
        }else{
            routeTag = savedInstanceState.getString("tag");
            routeName = savedInstanceState.getString("routeName");
        }
        setTitle(routeName);
        sendRequest();
    }

    private void sendRequest(){
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        xmlparser.addObserver(this);
        String url = "http://webservices.nextbus.com/service/publicXMLFeed?command=routeConfig&a=stl&r="+ routeTag + "&terse";
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
                sendRequest();
            }
        });
        queue.add(request);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("routeTag", routeTag);
    }

    public void refreshList(){
        StopSearchAdapter stopSearchAdapter;
        stopSearchAdapter = new StopSearchAdapter(this, R.layout.row_stop_search_list, stopList);
        listView.setAdapter(stopSearchAdapter);
        stopSearchAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Stop item = stopList.get(position);
                String stopId = item.getId();
                String stopName = item.getTitle();

                Intent stopSearchIntent = new Intent(getBaseContext(), StopDetailsActivity.class);
                stopSearchIntent.putExtra("stopId", stopId);
                stopSearchIntent.putExtra("routeTag", routeTag);
                stopSearchIntent.putExtra("routeName", routeName);
                stopSearchIntent.putExtra("stopName", stopName);
                startActivity(stopSearchIntent);
            }
        });
    }
    @Override
    public void update(Observable observable, Object o) {
        Log.d("stopfrag update", "entered");
        if(o instanceof StopList){
            stopList = (StopList) o;
        }
        refreshList();
    }
}
