package com.example.steven.stlbusarrivals.ui2.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.steven.stlbusarrivals.Constants;
import com.example.steven.stlbusarrivals.model2.Route;
import com.example.steven.stlbusarrivals.model2.RouteList;
import com.example.steven.stlbusarrivals.R;
import com.example.steven.stlbusarrivals.RequestSender;
import com.example.steven.stlbusarrivals.ui2.Activity.StopSearchActivity;
import com.example.steven.stlbusarrivals.ui2.Adapter.RouteSearchAdapter;

import java.util.Observable;
import java.util.Observer;


public class RouteSearchFragment extends Fragment implements Observer {

    private ListView listView;
    private static RouteList routeList;

    public RouteSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = "http://webservices.nextbus.com/service/publicXMLFeed?command=routeList&a=stl";
        RequestSender routeRequest = new RequestSender(getActivity(),Constants.ROUTE_XML, url);
        routeRequest.addObserver(this);
        routeRequest.sendRequest();
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
        RouteSearchAdapter routeSearchAdapter = new RouteSearchAdapter(getActivity(), R.layout.row_route_search_list, routeList);
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
        Log.d("searchfrag update", "entered");
        if(o instanceof RouteList){
            routeList = (RouteList) o;
            refreshList();
        }

    }
}
