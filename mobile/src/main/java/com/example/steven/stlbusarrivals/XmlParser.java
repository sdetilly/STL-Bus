package com.example.steven.stlbusarrivals;

import android.os.AsyncTask;
import android.util.Log;

import com.example.steven.stlbusarrivals.Model.Path;
import com.example.steven.stlbusarrivals.Model.PathBounds;
import com.example.steven.stlbusarrivals.Model.Point;
import com.example.steven.stlbusarrivals.Model.PathList;
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

    public void readRouteXml(final String xml)
            throws XmlPullParserException, IOException {
        new AsyncTask<Void, Void, Void>() {
            RouteList routeList = new RouteList();

            @Override
            protected Void doInBackground(Void... voids) {
                try{
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new StringReader(xml));
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                    } else if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("route")) {
                            Route route = new Route();
                            route.setTag(xpp.getAttributeValue(0));
                            route.setTitle(xpp.getAttributeValue(1));
                            routeList.add(route);
                        }
                    }
                    eventType = xpp.next();
                }
                }catch(Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                notifyObs(routeList);
            }

        }.execute();
    }

    public void readStopXml(final String xml)
            throws XmlPullParserException, IOException{
        new AsyncTask<Void, Void, Void>() {

            StopList stopList = new StopList();
            PathList pathList = new PathList();
            PathBounds pathBounds = new PathBounds();
            @Override
            protected Void doInBackground(Void... voids) {

                try {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xpp = factory.newPullParser();

                    xpp.setInput(new StringReader(xml));
                    int eventType = xpp.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_DOCUMENT) {
                        } else if (eventType == XmlPullParser.START_TAG) {
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
                    Log.d("doInBackground", "finished");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute (Void aVoid){
                super.onPostExecute(aVoid);
                Log.d("onPostExecute", "notifying observers");
                notifyObs(stopList);
                notifyObs(pathList);
                notifyObs(pathBounds);
            }
        }.execute();
    }

    public void readPrediction(final String xml)
            throws XmlPullParserException, IOException
    {
        new AsyncTask<Void, Void, Void>() {
            TimeList timeList = new TimeList();

            @Override
            protected Void doInBackground(Void... voids) {

                try {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xpp = factory.newPullParser();

                    xpp.setInput(new StringReader(xml));
                    int eventType = xpp.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_DOCUMENT) {
                        } else if (eventType == XmlPullParser.START_TAG) {
                            if (xpp.getName().equals("prediction")) {
                                TimePrediction timePrediction = new TimePrediction();
                                timePrediction.setTime(xpp.getAttributeValue(2));
                                timeList.add(timePrediction);
                            }
                        }
                        eventType = xpp.next();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                notifyObs(timeList);
            }
        }.execute();
    }

    public void readLocation(final String xml)
            throws XmlPullParserException, IOException
    {
        new AsyncTask<Void, Void, Void>() {
            VehiculeList vehiculeList = new VehiculeList();

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xpp = factory.newPullParser();

                    xpp.setInput(new StringReader(xml));
                    int eventType = xpp.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_DOCUMENT) {
                        } else if (eventType == XmlPullParser.START_TAG) {
                            if (xpp.getName().equals("vehicle")) {
                                Vehicule vehicule = new Vehicule();
                                vehicule.setLongitude(xpp.getAttributeValue(4));
                                vehicule.setLatitude(xpp.getAttributeValue(3));
                                vehiculeList.add(vehicule);
                            }
                        }
                        eventType = xpp.next();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                notifyObs(vehiculeList);
            }
        }.execute();
    }

    public void notifyObs(Object o){
        setChanged();
        notifyObservers(o);
    }
}
