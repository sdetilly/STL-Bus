package com.example.steven.stlbusarrivals.model2;

import com.google.android.gms.wearable.DataMap;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Steven on 2016-02-03.
 */
public class PathList extends ArrayList<Path> implements Serializable{

    public DataMap putData(){
        DataMap map = new DataMap();
        for(int i = 0; i<this.size(); i++){
            map.putDataMap("path"+i,this.get(i).putData());
        }
        return map;
    }
}
