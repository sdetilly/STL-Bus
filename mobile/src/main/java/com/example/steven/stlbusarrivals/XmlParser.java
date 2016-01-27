package com.example.steven.stlbusarrivals;

import android.support.annotation.NonNull;
import android.util.Xml;

import com.example.steven.stlbusarrivals.Model.Route;
import com.example.steven.stlbusarrivals.Model.RouteList;
import com.example.steven.stlbusarrivals.UI.Fragments.SearchFragment;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;

/**
 * Created by Steven on 2016-01-25.
 */
public class XmlParser extends Observable{

    private static RouteList routeList = new RouteList();

    public RouteList readXml(String xml)
            throws XmlPullParserException, IOException
    {
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
                    route.setTag(xpp.getAttributeName(0));
                    route.setTitle(xpp.getAttributeName(1));
                    routeList.add(route);
                }
            }
            eventType = xpp.next();
        }
        System.out.println("End document");
        notifyObs();
        return routeList;

    }
    public void notifyObs(){
        XmlParser.this.setChanged();
        XmlParser.this.notifyObservers(SearchFragment.class.getName());
    }
}
