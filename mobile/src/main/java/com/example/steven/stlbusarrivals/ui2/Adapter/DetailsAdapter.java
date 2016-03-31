package com.example.steven.stlbusarrivals.ui2.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.steven.stlbusarrivals.model2.Details;
import com.example.steven.stlbusarrivals.R;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Steven on 2016-01-28.
 */
public class DetailsAdapter extends ArrayAdapter<Details> {

    private LayoutInflater inflater;
    private String prediction;

    public DetailsAdapter(Context context, int rowLayoutResourceId, ArrayList<Details> list) {
        super(context, rowLayoutResourceId, list);
        this.inflater = LayoutInflater.from(context);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int position, View view, ViewGroup parent) {

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
        prediction = item.getPrediction();

        if(prediction != null){
            Calendar c = Calendar.getInstance();
            int currentHour = c.get(Calendar.HOUR_OF_DAY);
            int currentMinutes = c.get(Calendar.MINUTE);
                int predictedMinutes = currentMinutes + Integer.valueOf(prediction);
                while(predictedMinutes >= 60){
                    currentHour++;
                    predictedMinutes = predictedMinutes - 60;
                }
                if(predictedMinutes < 10){
                    holder.arrivalTime.setText(currentHour + ":0"+ predictedMinutes);
                    holder.prediction.setText("   Next bus is in " + prediction + " minutes");
                }else {
                    holder.arrivalTime.setText(currentHour + ":"+ predictedMinutes);
                    holder.prediction.setText("   Next bus is in " + prediction + " minutes");
                }
        }else{
            holder.arrivalTime.setText(" there are no buses at this current time");
        }
        holder.routeName.setText(item.getRouteName());
        holder.stop.setText(item.getStopName());
        return view;
    }

    static class ViewHolder {
        TextView routeName;
        TextView stop;
        TextView arrivalTime;
        TextView prediction;
    }
}
