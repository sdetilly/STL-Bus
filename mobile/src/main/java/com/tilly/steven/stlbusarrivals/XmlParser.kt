package com.tilly.steven.stlbusarrivals

import android.util.Log
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import com.tilly.steven.stlbusarrivals.model.*
import com.tilly.steven.stlbusarrivals.network.NetworkCallback
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
    fun readRouteXml(xml: String, callback: NetworkCallback){
        launchUI {
            val list = async {
                val routeList = RouteList()
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
                routeList
            }.await()
            callback.onRouteListLoaded(list)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun readStopXml(xml: String, callback: NetworkCallback) {
        launchUI {
            val stopList = StopList()
            val pathList = PathList()
            val pathBounds = PathBounds()

            val triple = async {
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
                Triple(stopList, pathList, pathBounds)
            }.await()
            callback.onStopListLoaded(triple.first)
            callback.onPathListLoaded(triple.second)
            callback.onPathBoundsLoaded(triple.third)
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
    fun readLocation(xml: String, callback: NetworkCallback) {
        launchUI {
            val vehiculeList = VehiculeList()
            val list = async {
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
                vehiculeList
            }.await()
            callback.onVehicleListLoaded(list)
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
                        message += (HtmlCompat.fromHtml(text, FROM_HTML_MODE_LEGACY).toString() + "\n \n")
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
