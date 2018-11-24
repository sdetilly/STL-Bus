package com.tilly.steven.stlbusarrivals.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tilly.steven.stlbusarrivals.Model.Stop;
import com.tilly.steven.stlbusarrivals.R;

import java.util.ArrayList;

/**
 * Created by Steven on 2016-01-28.
 */
public class StopSearchAdapter extends ArrayAdapter<Stop> {

    private LayoutInflater inflater;
    private Context context;

    public StopSearchAdapter(Context context, int rowLayoutResourceId, ArrayList<Stop> list) {
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
            view = inflater.inflate(R.layout.row_stop_search_list, parent, false);
            holder = new ViewHolder();
            holder.stopName = (TextView) view.findViewById(R.id.tv_row_stop_name);
            view.setTag(holder);
        }

        Stop item = getItem(position);
        holder.stopName.setText(item.getTitle());

        return view;
    }

    static class ViewHolder {
        TextView stopName;
    }
}