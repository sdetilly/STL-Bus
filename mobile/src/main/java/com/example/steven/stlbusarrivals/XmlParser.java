package com.example.steven.stlbusarrivals;

import android.os.AsyncTask;

import com.example.steven.stlbusarrivals.model.Path;
import com.example.steven.stlbusarrivals.model.PathBounds;
import com.example.steven.stlbusarrivals.model.Point;
import com.example.steven.stlbusarrivals.model.PathList;
import com.example.steven.stlbusarrivals.model.Route;
import com.example.steven.stlbusarrivals.model.RouteList;
import com.example.steven.stlbusarrivals.model.Stop;
import com.example.steven.stlbusarrivals.model.StopList;
import com.example.steven.stlbusarrivals.model.TimeList;
import com.example.steven.stlbusarrivals.model.TimePrediction;
import com.example.steven.stlbusarrivals.model.Vehicule;
import com.example.steven.stlbusarrivals.model.VehiculeList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.Observable;

/**
 * Created by Steven on 2016-01-25.
 */
public class XmlParser extends Observable{

    public void readXml(final int xmlType, final String xml){
        new AsyncTask<Void, Void, Void>(){
            RouteList routeList;
            StopList stopList;
            PathList pathList;
            PathBounds pathBounds;
            TimeList timeList;
            VehiculeList vehiculeList;
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xpp = factory.newPullParser();

                    xpp.setInput(new StringReader(xml));
                    int eventType = xpp.getEventType();
                    switch(xmlType){
                        case Constants.ROUTE_XML:
                            routeList = new RouteList();
                            while (eventType != XmlPullParser.END_DOCUMENT) {
                                if (eventType == XmlPullParser.START_TAG) {
                                    if (xpp.getName().equals("route")) {
                                        Route route = new Route();
                                        route.setTag(xpp.getAttributeValue(0));
                                        route.setTitle(xpp.getAttributeValue(1));
                                        routeList.add(route);
                                    }
                                }
                                eventType = xpp.next();
                            }
                            return null;
                        case Constants.STOP_XML:
                            stopList = new StopList();
                            pathList = new PathList();
                            pathBounds = new PathBounds();
                            while (eventType != XmlPullParser.END_DOCUMENT) {
                                if (eventType == XmlPullParser.START_TAG) {
                                    if(xpp.getName().equals("route")){
                                        pathBounds.setLatMin(Double.valueOf(xpp.getAttributeValue(4)));
                                        pathBounds.setLatMax(Double.valueOf(xpp.getAttributeValue(5)));
                                        pathBounds.setLongMin(Double.valueOf(xpp.getAttributeValue(6)));
                                        pathBounds.setLongMax(Double.valueOf(xpp.getAttributeValue(7)));
                                    }
                                    if (xpp.getName().equals("stop") && xpp.getAttributeCount() > 1) {
                                        Stop stop = new Stop();
                                        stop.setTag(xpp.getAttributeValue(0));
                                        stop.setTitle(xpp.getAttributeValue(1));
                                        stop.setId(xpp.getAttributeValue(4));
                                        stopList.add(stop);
                                    }
                                    if(xpp.getName().equals("path")){
                                        Path path = new Path();
                                        while(eventType != XmlPullParser.END_TAG || (xpp.getName() !=null && xpp.getName().equals("point"))) {
                                            if(xpp.getName() != null && xpp.getName().equals("point")) {
                                                Point point = new Point();
                                                point.setLatitude(Double.valueOf(xpp.getAttributeValue(0)));
                                                point.setLongitude(Double.valueOf(xpp.getAttributeValue(1)));
                                                path.add(point);
                                            }
                                            eventType = xpp.next();
                                        }
                                        pathList.add(path);
                                    }
                                }
                                eventType = xpp.next();
                            }
                            return null;
                        case Constants.PREDICTION_XML:
                            timeList = new TimeList();
                            while (eventType != XmlPullParser.END_DOCUMENT) {
                                if (eventType == XmlPullParser.START_TAG) {
                                    if (xpp.getName().equals("prediction")) {
                                        TimePrediction timePrediction = new TimePrediction();
                                        timePrediction.setTime(xpp.getAttributeValue(2));
                                        timeList.add(timePrediction);
                                    }
                                }
                                eventType = xpp.next();
                            }
                            return null;
                        case Constants.LOCATION_XML:
                            vehiculeList = new VehiculeList();
                            while (eventType != XmlPullParser.END_DOCUMENT) {
                                if (eventType == XmlPullParser.START_TAG) {
                                    if (xpp.getName().equals("vehicle")) {
                                        Vehicule vehicule = new Vehicule();
                                        vehicule.setLongitude(xpp.getAttributeValue(4));
                                        vehicule.setLatitude(xpp.getAttributeValue(3));
                                        vehiculeList.add(vehicule);
                                    }
                                }
                                eventType = xpp.next();
                            }
                            return null;
                        default:
                            return null;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                switch(xmlType){
                    case Constants.ROUTE_XML:
                        notifyObs(routeList);
                        break;
                    case Constants.STOP_XML:
                        notifyObs(stopList);
                        notifyObs(pathList);
                        notifyObs(pathBounds);
                        break;
                    case Constants.PREDICTION_XML:
                        notifyObs(timeList);
                        break;
                    case Constants.LOCATION_XML:
                        notifyObs(vehiculeList);
                        break;
                    default:
                        break;
                }
            }
        }.execute();
    }

    public void notifyObs(Object o){
        setChanged();
        notifyObservers(o);
    }
}
