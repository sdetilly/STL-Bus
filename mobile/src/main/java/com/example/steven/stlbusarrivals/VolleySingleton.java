package com.example.steven.stlbusarrivals;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Steven on 2016-01-22.
 */
public class VolleySingleton {

    private static VolleySingleton instance;
    private RequestQueue requestQueue;

    private VolleySingleton(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public static VolleySingleton getInstance(Context context) {
        if (instance == null) {
            instance = new VolleySingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
