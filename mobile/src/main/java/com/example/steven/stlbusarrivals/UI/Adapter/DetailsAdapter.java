package com.example.steven.stlbusarrivals.UI.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.steven.stlbusarrivals.Model.Details;
import com.example.steven.stlbusarrivals.Model.TimeList;
import com.example.steven.stlbusarrivals.R;
import com.example.steven.stlbusarrivals.VolleySingleton;
import com.example.steven.stlbusarrivals.XmlParser;

import java.util.ArrayList;

/**
 * Created by Steven on 2016-01-28.
 */
public class DetailsAdapter extends ArrayAdapter<Details>{

    private LayoutInflater inflater;
    private Context context;
    private String routeTag, stopId, routeName, stopName;
    private ArrayList<Details> detailsList;

    public DetailsAdapter(Context context, int rowLayoutResourceId, ArrayList<Details> list) {
        super(context, rowLayoutResourceId, list);
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        detailsList = list;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        routeTag = detailsList.get(position).getTag();
        stopId = detailsList.get(position).getStopId();
        routeName = detailsList.get(position).getRouteName();
        stopName = detailsList.get(position).getStopName();

        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.row_favorites, parent, false);
            holder = new ViewHolder();
            holder.routeName = (TextView) view.findViewById(R.id.tv_row_favorite_route);
            holder.stop = (TextView) view.findViewById(R.id.tv_row_favorite_stop);
            holder.arrivalTime = (TextView) view.findViewById(R.id.tv_row_favorite_arrival_time);
            holder.prediction = (TextView) view.findViewById(R.id.tv_row_favorite_prediction);
            view.setTag(holder);
        }

        Details item = getItem(position);
        item.getNetPrediction();
        holder.routeName.setText(item.getTag());
        holder.stop.setText(" : "+ stopName);
        //holder.arrivalTime.setText("Next bus in " + item.getTag() + " minutes");
        holder.prediction.setText(item.getPrediction());

        return view;
    }

    static class ViewHolder {
        TextView routeName;
        TextView stop;
        TextView arrivalTime;
        TextView prediction;
    }
}
