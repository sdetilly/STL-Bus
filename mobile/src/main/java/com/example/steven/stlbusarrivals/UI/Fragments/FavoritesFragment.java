package com.example.steven.stlbusarrivals.UI.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.steven.stlbusarrivals.Dao.DatabaseHelper;
import com.example.steven.stlbusarrivals.Model.Details;
import com.example.steven.stlbusarrivals.Model.Stop;
import com.example.steven.stlbusarrivals.Model.TimeList;
import com.example.steven.stlbusarrivals.R;
import com.example.steven.stlbusarrivals.UI.Activity.MainActivity;
import com.example.steven.stlbusarrivals.UI.Activity.StopDetailsActivity;
import com.example.steven.stlbusarrivals.UI.Adapter.DetailsAdapter;
import com.example.steven.stlbusarrivals.VolleySingleton;
import com.example.steven.stlbusarrivals.XmlParser;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class FavoritesFragment extends Fragment implements Observer{

    RequestQueue queue;
    private DatabaseHelper databaseHelper = null;
    ListView listView;
    DetailsAdapter detailsAdapter;
    ArrayList<Details> detailsList;
    private int lastClickedDetailsId;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_favorites, container, false);
        listView = (ListView)v.findViewById(R.id.list_favorite);
        detailsList = getAllOrderedDetails();
        for(int i=0; i<detailsList.size(); i++){
            Log.d("favoritefrag", "adding observers...");
            detailsList.get(i).addObserver(this); // i think i need to create different variable for it to work....
        }
        detailsAdapter = new DetailsAdapter(getActivity(),R.layout.row_favorites, detailsList);
        listView.setAdapter(detailsAdapter);
        detailsAdapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Details item = detailsList.get(position);
                String stopId = item.getStopId();
                String stopName = item.getStopName();
                String routeTag = item.getTag();
                String routeName = item.getRouteName();

                Intent stopSearchIntent = new Intent(getActivity(), StopDetailsActivity.class);
                stopSearchIntent.putExtra("stopId", stopId);
                stopSearchIntent.putExtra("routeTag", routeTag);
                stopSearchIntent.putExtra("routeName", routeName);
                stopSearchIntent.putExtra("stopName", stopName);
                startActivity(stopSearchIntent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
                final Details item = (Details) parent.getItemAtPosition(position);
                PopupMenu popup = new PopupMenu(getActivity(), view);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.menu_favorites, popup.getMenu());
                lastClickedDetailsId = item.getId();

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.item_delete) {
                            Log.d("MainActivity", "will delete timer...");
                            deleteDetails();
                        }
                        return true;
                    }
                });
                popup.show();
                return true;
            }
        });
        return v;
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
    private DatabaseHelper getHelper(){
        if(databaseHelper == null){
            databaseHelper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        }
        return databaseHelper;
    }

    public void onDestroy(){
        super.onDestroy();
        if(databaseHelper!=null){
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        detailsList = getAllOrderedDetails();
        this.detailsAdapter.clear();
        this.detailsAdapter.addAll(detailsList);
        this.detailsAdapter.notifyDataSetChanged();
    }

    private void deleteDetails(){
        getHelper().getDetailsDao().deleteById(this.lastClickedDetailsId);
        ArrayList<Details> details = getAllOrderedDetails();
        this.detailsAdapter.clear();
        this.detailsAdapter.addAll(details);
        this.detailsAdapter.notifyDataSetChanged();
        Log.i(MainActivity.class.getName(), String.valueOf(this.lastClickedDetailsId));
    }

    @Override
    public void update(Observable observable, Object o) {
        Log.d("favoritesfrag update", "entered");
            ArrayList<Details> details = getAllOrderedDetails();
            this.detailsAdapter.clear();
            this.detailsAdapter.addAll(details);
            this.detailsAdapter.notifyDataSetChanged();

    }
}
