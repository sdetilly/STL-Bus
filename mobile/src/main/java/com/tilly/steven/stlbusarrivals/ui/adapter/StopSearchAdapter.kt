package com.tilly.steven.stlbusarrivals.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.tilly.steven.stlbusarrivals.R
import com.tilly.steven.stlbusarrivals.model.Stop
import java.util.*

/**
 * Created by Steven on 2016-01-28.
 */
class StopSearchAdapter(ctx: Context, rowLayoutResourceId: Int, list: ArrayList<Stop>) : ArrayAdapter<Stop>(ctx, rowLayoutResourceId, list) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    @SuppressLint("DefaultLocale")
    override fun getView(position: Int, view1: View?, parent: ViewGroup): View {
        var view = view1

        val holder: ViewHolder
        if (view != null) {
            holder = view.tag as ViewHolder
        } else {
            view = inflater.inflate(R.layout.row_stop_search_list, parent, false)!!
            holder = ViewHolder()
            holder.stopName = view.findViewById<View>(R.id.tv_row_stop_name) as TextView
            view.tag = holder
        }

        val item = getItem(position)
        holder.stopName.text = item?.title
        return view
    }

    internal class ViewHolder {
        lateinit var stopName: TextView
    }
}