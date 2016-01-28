package com.example.steven.stlbusarrivals.UI.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.steven.stlbusarrivals.Model.Route;
import com.example.steven.stlbusarrivals.R;

import java.util.ArrayList;

/**
 * Created by Steven on 2016-01-26.
 */
public class RouteSearchAdapter extends ArrayAdapter<Route> {

    private LayoutInflater inflater;
    private Context context;

    public RouteSearchAdapter(Context context, int rowLayoutResourceId, ArrayList<Route> list) {
        super(context, rowLayoutResourceId, list);
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.row_route_search_list, parent, false);
            holder = new ViewHolder();
            holder.routeName = (TextView) view.findViewById(R.id.tv_row_route_name);
            view.setTag(holder);
        }

        Route item = getItem(position);
        holder.routeName.setText(item.getName());

        return view;
    }

    static class ViewHolder {
        TextView routeName;
    }
}
