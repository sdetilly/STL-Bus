package com.tilly.steven.stlbusarrivals;

import android.content.Context;
import android.widget.Toast;

public class Utils {

    public static void toast(Context ctx, String message){
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }
}
