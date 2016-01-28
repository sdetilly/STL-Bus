package com.example.steven.stlbusarrivals;

import com.example.steven.stlbusarrivals.Model.Route;
import com.example.steven.stlbusarrivals.Model.RouteList;

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

    public void readXml(String xml)
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
        System.out.println("End document");
        notifyObs(routeList);

    }
    public void notifyObs(RouteList rl){
        setChanged();
        notifyObservers(rl);
    }
}
