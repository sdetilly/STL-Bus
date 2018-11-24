package com.tilly.steven.stlbusarrivals;

import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;

/**
 * Created by Steven on 2016-02-06.
 */
public class Path extends ArrayList<Point> {

    public Path(DataMap map){
        if(map.size() > 0) {
            for (int i = 0; i < map.size(); i++) {
                DataMap pointMap;
                pointMap = map.getDataMap("point" + i);
                this.add(new Point(pointMap));
            }
        }
    }
}
