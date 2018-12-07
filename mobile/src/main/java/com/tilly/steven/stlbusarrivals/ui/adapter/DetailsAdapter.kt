package com.tilly.steven.stlbusarrivals.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.tilly.steven.stlbusarrivals.R
import com.tilly.steven.stlbusarrivals.model.Details
import java.util.*

/**
 * Created by Steven on 2016-01-28.
 */
class DetailsAdapter(private val ctx: Context, rowLayoutResourceId: Int, list: List<Details>) : ArrayAdapter<Details>(ctx, rowLayoutResourceId, list) {

    private val inflater: LayoutInflater = LayoutInflater.from(ctx)

    @SuppressLint("DefaultLocale")
    override fun getView(position: Int, view1: View?, parent: ViewGroup): View {
        var view = view1

        val holder: ViewHolder
        if (view != null) {
            holder = view.tag as ViewHolder
        } else {
            view = inflater.inflate(R.layout.row_favorites, parent, false)!!
            holder = ViewHolder()
            holder.routeName = view.findViewById<View>(R.id.tv_row_favorite_route) as TextView
            holder.stop = view.findViewById<View>(R.id.tv_row_favorite_stop) as TextView
            holder.arrivalTime = view.findViewById<View>(R.id.tv_row_favorite_arrival_time) as TextView
            holder.prediction = view.findViewById<View>(R.id.tv_row_favorite_prediction) as TextView
            view.tag = holder
        }

        val item = getItem(position)
        val prediction = item?.prediction

        if (prediction != null) {
            val c = Calendar.getInstance()
            var currentHour = c.get(Calendar.HOUR_OF_DAY)
            val currentMinutes = c.get(Calendar.MINUTE)
            var predictedMinutes = currentMinutes + Integer.valueOf(prediction)
            while (predictedMinutes >= 60) {
                currentHour++
                predictedMinutes -= 60
            }
            if (predictedMinutes < 10) {
                holder.arrivalTime.text = "$currentHour:0$predictedMinutes"
            } else {
                holder.arrivalTime.text = "$currentHour:$predictedMinutes"
            }
            holder.prediction.text = ctx.getString(R.string.next_bus, prediction)

        } else {
            holder.arrivalTime.text = ctx.getString(R.string.no_bus)
        }
        holder.routeName.text = item?.routeName
        holder.stop.text = item?.stopName
        return view
    }

    internal class ViewHolder {
        lateinit var routeName: TextView
        lateinit var stop: TextView
        lateinit var arrivalTime: TextView
        lateinit var prediction: TextView
    }
}
