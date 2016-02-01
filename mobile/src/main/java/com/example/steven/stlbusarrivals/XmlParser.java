package com.example.steven.stlbusarrivals;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.steven.stlbusarrivals.Model.Route;
import com.example.steven.stlbusarrivals.Model.RouteList;
import com.example.steven.stlbusarrivals.Model.Stop;
import com.example.steven.stlbusarrivals.Model.StopList;
import com.example.steven.stlbusarrivals.Model.TimeList;
import com.example.steven.stlbusarrivals.Model.TimePrediction;
import com.example.steven.stlbusarrivals.Model.Vehicule;
import com.example.steven.stlbusarrivals.Model.VehiculeList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.Observable;

/**
 * Created by Steven on 2016-01-25.
 */
public class XmlParser extends Observable{

    public void readRouteXml(String xml)
            throws XmlPullParserException, IOException
    {
        RouteList routeList = new RouteList();
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput( new StringReader( xml ) );
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_DOCUMENT) {
            } else if(eventType == XmlPullParser.START_TAG) {
                if(xpp.getName().equals("route")){
                    Route route = new Route();
                    route.setTag(xpp.getAttributeValue(0));
                    route.setTitle(xpp.getAttributeValue(1));
                    routeList.add(route);
                }
            }
            eventType = xpp.next();
        }
        notifyObs(routeList);

    }

    public void readStopXml(String xml)
            throws XmlPullParserException, IOException
    {
        StopList stopList = new StopList();
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput( new StringReader( xml ) );
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_DOCUMENT) {
            } else if(eventType == XmlPullParser.START_TAG) {
                if(xpp.getName().equals("stop") && xpp.getAttributeCount()>1){
                    Stop stop = new Stop();
                    stop.setTag(xpp.getAttributeValue(0));
                    stop.setTitle(xpp.getAttributeValue(1));
                    stop.setId(xpp.getAttributeValue(4));
                    stopList.add(stop);
                }
            }
                eventType = xpp.next();
        }
        notifyObs(stopList);
    }

    public void readPrediction(String xml)
            throws XmlPullParserException, IOException
    {
        TimeList timeList = new TimeList();
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput( new StringReader( xml ) );
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_DOCUMENT) {
            } else if(eventType == XmlPullParser.START_TAG) {
                if(xpp.getName().equals("prediction")){
                    TimePrediction timePrediction = new TimePrediction();
                    timePrediction.setTime(xpp.getAttributeValue(2));
                    timeList.add(timePrediction);
                }
            }
            eventType = xpp.next();
        }
        notifyObs(timeList);
    }

    public void readLocation(String xml)
            throws XmlPullParserException, IOException
    {
        VehiculeList vehiculeList = new VehiculeList();
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput( new StringReader( xml ) );
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_DOCUMENT) {
            } else if(eventType == XmlPullParser.START_TAG) {
                System.out.println(xpp.getName());
                if(xpp.getName().equals("vehicle")){
                    Vehicule vehicule = new Vehicule();
                    vehicule.setLongitude(xpp.getAttributeValue(4));
                    vehicule.setLatitude(xpp.getAttributeValue(3));
                    vehiculeList.add(vehicule);
                }
            }
            eventType = xpp.next();
        }
        notifyObs(vehiculeList);
    }

    public void notifyObs(Object o){
        setChanged();
        notifyObservers(o);
    }
}
