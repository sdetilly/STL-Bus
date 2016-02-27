package com.example.steven.stlbusarrivals;

import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;

/**
 * Created by Steven on 2016-02-06.
 */
public class PathList extends ArrayList<Path> {

    public void setData(DataMap map){
        if(map.size() > 0) {
            for (int i = 0; i < map.size(); i++) {
                DataMap pathMap;
                pathMap = map.getDataMap("path" + i);
                this.add(new Path(pathMap));
            }
        }
    }
}
