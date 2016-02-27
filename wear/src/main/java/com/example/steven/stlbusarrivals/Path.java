package com.example.steven.stlbusarrivals;

import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;

/**
 * Created by Steven on 2016-02-06.
 */
public class Path extends ArrayList<Point> {

    public void setData(DataMap map){
        for(int i = 0; i<this.size(); i++){
            DataMap pointMap;
            pointMap = map.getDataMap("point"+i);
            this.get(i).setData(pointMap);
        }
    }
}
