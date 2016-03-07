package com.example.steven.stlbusarrivals.UI.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.example.steven.stlbusarrivals.Constants;
import com.example.steven.stlbusarrivals.Model.Route;
import com.example.steven.stlbusarrivals.Model.RouteList;
import com.example.steven.stlbusarrivals.R;
import com.example.steven.stlbusarrivals.RequestSender;
import com.example.steven.stlbusarrivals.UI.Activity.StopSearchActivity;
import com.example.steven.stlbusarrivals.UI.Adapter.RouteSearchAdapter;
import com.example.steven.stlbusarrivals.VolleySingleton;
import com.example.steven.stlbusarrivals.XmlParser;
import java.util.Observable;
import java.util.Observer;


public class RouteSearchFragment extends Fragment implements Observer {

    private ListView listView;
    private static RouteList routeList;
    private RouteSearchAdapter routeSearchAdapter;
    private XmlParser xmlparser = new XmlParser();
    private RequestSender routeRequest;

    public RouteSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //xmlparser.addObserver(this);
        //sendRequest();
        String url = "http://webservices.nextbus.com/service/publicXMLFeed?command=routeList&a=stl";
        routeRequest = new RequestSender(getActivity(),Constants.ROUTE_XML, url);
        routeRequest.addObserver(this);
        routeRequest.sendRequest();
    }

    private void sendRequest(){
        RequestQueue queue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        String url = "http://webservices.nextbus.com/service/publicXMLFeed?command=routeList&a=stl";
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // we got the response, now our job is to handle it
                //parseXmlResponse(response);
                try{
                    //xmlparser.readRouteXml(response);
                    xmlparser.readXml(Constants.ROUTE_XML, response);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_route_search, container, false);
        listView = (ListView) v.findViewById(R.id.list_route_search);
        return v;
    }

    public void refreshList(){
        routeSearchAdapter = new RouteSearchAdapter(getActivity(), R.layout.row_route_search_list, routeList);
        listView.setAdapter(routeSearchAdapter);
        routeSearchAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Route item = routeList.get(position);
                String tag = item.getTag();
                String routeName = item.getTitle();

                Intent stopSearchIntent = new Intent(getActivity(), StopSearchActivity.class);
                stopSearchIntent.putExtra("tag",tag);
                stopSearchIntent.putExtra("routeName",routeName);
                getActivity().startActivity(stopSearchIntent);
            }
        });
    }
    @Override
    public void update(Observable observable, Object o) {
        Log.d("searchfrag update","entered");
        if(o instanceof RouteList){
            routeList = (RouteList) o;
        }
        refreshList();
    }
}
