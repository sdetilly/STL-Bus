package com.example.steven.stlbusarrivals;

import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;

/**
 * Created by Steven on 2016-02-06.
 */
public class PathList extends ArrayList<Path> {

    public void setData(DataMap map){
        for(int i = 0; i<this.size(); i++){
            DataMap pathMap;
            pathMap = map.getDataMap("path"+i);
            this.get(i).setData(pathMap);
        }
    }
}
