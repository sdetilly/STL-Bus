package com.example.steven.stlbusarrivals.UI.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.steven.stlbusarrivals.Model.RouteList;
import com.example.steven.stlbusarrivals.R;
import com.example.steven.stlbusarrivals.UI.Adapter.SearchAdapter;
import com.example.steven.stlbusarrivals.VolleySingleton;
import com.example.steven.stlbusarrivals.XmlParser;

import java.util.Observable;
import java.util.Observer;


public class SearchFragment extends Fragment implements Observer {

    private ListView listView;
    private static RouteList routeList;
    private SearchAdapter searchAdapter;
    private XmlParser xmlparser = new XmlParser();
    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RequestQueue queue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        xmlparser.addObserver(this);
        String url = "http://webservices.nextbus.com/service/publicXMLFeed?command=routeList&a=stl";
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // we got the response, now our job is to handle it
                //parseXmlResponse(response);
                try{
                    xmlparser.readXml(response);
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //something happened, treat the error.
            }
        });
        queue.add(request);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_search, container, false);
        listView = (ListView) v.findViewById(R.id.list);
        return v;
    }

    public void refreshList(){
        searchAdapter = new SearchAdapter(getActivity(), R.layout.row_search_list, routeList);
        listView.setAdapter(searchAdapter);
        searchAdapter.notifyDataSetChanged();
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
