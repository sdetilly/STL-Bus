package com.example.steven.stlbusarrivals;

import android.util.Xml;

import com.example.steven.stlbusarrivals.Model.Route;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steven on 2016-01-25.
 */
public class XmlParser {

    public static void readXml(String xml)
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
                    System.out.print(xpp.getName() + " ");
                    System.out.print(xpp.getAttributeName(0) + " ");
                    System.out.print(xpp.getAttributeValue(0) + " ");
                    System.out.print(xpp.getAttributeName(1) + " ");
                    System.out.println(xpp.getAttributeValue(1));
                }
            } else if(eventType == XmlPullParser.END_TAG) {
                System.out.println("End tag "+xpp.getName());
            } else if(eventType == XmlPullParser.TEXT) {
                System.out.println("Text "+xpp.getText());
            }
            eventType = xpp.next();
        }
        System.out.println("End document");
    }
}
