package com.example.steven.stlbusarrivals.Model;

/**
 * Created by Steven on 2016-01-28.
 */
public class Stop {
    String tag;

    String title;

    public Stop(){    }

    public String getTag(){return tag;}

    public String getTitle(){return title;}

    public String getName(){return tag + " " + title;}

    public void setTag(String tag){this.tag = tag;}

    public void setTitle(String title){this.title = title;}
}
