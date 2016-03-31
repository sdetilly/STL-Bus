package com.example.steven.stlbusarrivals.model;

/**
 * Created by Steven on 2016-01-28.
 */
public class Stop {
    String tag;

    String title;

    String id;

    public Stop(){    }

    public String getTag(){return tag;}

    public String getTitle(){return title;}

    public String getId(){return id;}

    public String getName(){return tag + " " + title;}

    public void setTag(String tag){this.tag = tag;}

    public void setTitle(String title){this.title = title;}

    public void setId(String id){this.id = id;}
}
