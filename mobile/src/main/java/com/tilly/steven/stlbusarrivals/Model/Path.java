package com.tilly.steven.stlbusarrivals.Model;

import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;

/**
 * Created by Steven on 2016-02-04.
 */
public class Path extends ArrayList<Point> {

    public DataMap putData(){
        DataMap map = new DataMap();
        for(int i = 0; i<this.size(); i++){
            map.putDataMap("point"+i,this.get(i).putData());
        }
        return map;
    }
}
