package com.tilly.steven.stlbusarrivals

import android.text.Html
import android.util.Log
import com.tilly.steven.stlbusarrivals.model.*
import kotlinx.coroutines.async
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.StringReader
import java.util.*

/**
 * Created by Steven on 2016-01-25.
 */
class XmlParser : Observable() {

    @Throws(XmlPullParserException::class, IOException::class)
    fun readRouteXml(xml: String) {
        launchUI {
            val routeList = RouteList()
            async {
                val factory = XmlPullParserFactory.newInstance()
                factory.isNamespaceAware = true
                val xpp = factory.newPullParser()

                xpp.setInput(StringReader(xml))
                var eventType = xpp.eventType
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                    } else if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.name == "route") {
                            val route = Route()
                            route.tag = xpp.getAttributeValue(0)
                            route.title = xpp.getAttributeValue(1)
                            routeList.add(route)
                        }
                    }
                    eventType = xpp.next()
                }
            }.await()
            notifyObs(routeList)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun readStopXml(xml: String) {
        launchUI {
            val stopList = StopList()
            val pathList = PathList()
            val pathBounds = PathBounds()

            async {
                val factory = XmlPullParserFactory.newInstance()
                factory.isNamespaceAware = true
                val xpp = factory.newPullParser()

                xpp.setInput(StringReader(xml))
                var eventType = xpp.eventType
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                    } else if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.name == "route") {
                            pathBounds.latMin = java.lang.Double.valueOf(xpp.getAttributeValue(4))
                            pathBounds.latMax = java.lang.Double.valueOf(xpp.getAttributeValue(5))
                            pathBounds.longMin = java.lang.Double.valueOf(xpp.getAttributeValue(6))
                            pathBounds.longMax = java.lang.Double.valueOf(xpp.getAttributeValue(7))
                        }
                        if (xpp.name == "stop" && xpp.attributeCount > 1) {
                            val stop = Stop()
                            stop.tag = xpp.getAttributeValue(0)
                            stop.title = xpp.getAttributeValue(1)
                            stop.id = xpp.getAttributeValue(4)
                            stopList.add(stop)
                        }
                        if (xpp.name == "path") {
                            val path = Path()
                            while (eventType != XmlPullParser.END_TAG || xpp.name != null && xpp.name == "point") {
                                if (xpp.name != null && xpp.name == "point") {
                                    val point = Point()
                                    point.latitude = java.lang.Double.valueOf(xpp.getAttributeValue(0))
                                    point.longitude = java.lang.Double.valueOf(xpp.getAttributeValue(1))
                                    path.add(point)
                                }
                                eventType = xpp.next()
                            }
                            pathList.add(path)
                        }
                    }
                    eventType = xpp.next()
                }
                Log.d("doInBackground", "finished")
            }.await()
            Log.d("onPostExecute", "notifying observers")
            notifyObs(stopList)
            notifyObs(pathList)
            notifyObs(pathBounds)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun readPrediction(xml: String) {
        launchUI{
            val timeList = TimeList()

            async {
                val factory = XmlPullParserFactory.newInstance()
                factory.isNamespaceAware = true
                val xpp = factory.newPullParser()

                xpp.setInput(StringReader(xml))
                var eventType = xpp.eventType
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                    } else if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.name == "prediction") {
                            val timePrediction = TimePrediction()
                            timePrediction.time = xpp.getAttributeValue(2)
                            timeList.add(timePrediction)
                        }
                    }
                    eventType = xpp.next()
                }
            }.await()
            notifyObs(timeList)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun readLocation(xml: String) {
        launchUI {
            val vehiculeList = VehiculeList()

            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val xpp = factory.newPullParser()

            xpp.setInput(StringReader(xml))
            var eventType = xpp.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                } else if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.name == "vehicle") {
                        val vehicule = Vehicule()
                        vehicule.longitude = xpp.getAttributeValue(4)
                        vehicule.latitude = xpp.getAttributeValue(3)
                        vehiculeList.add(vehicule)
                    }
                }
                eventType = xpp.next()
            }
            notifyObs(vehiculeList)

        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun readMessages(xml: String) {
        launchUI {
            var message = ""

            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val xpp = factory.newPullParser()

            xpp.setInput(StringReader(xml))
            var eventType = xpp.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                } else if (eventType == XmlPullParser.TEXT) {
                    val text = xpp.text
                    if (text != null && !text.isEmpty() && !text.contains("\n")) {
                        message += (Html.fromHtml(text).toString() + "\n \n")
                    }
                }
                eventType = xpp.next()
            }
            notifyObs(message)
        }
    }

    fun notifyObs(o: Any) {
        setChanged()
        notifyObservers(o)
    }
}
