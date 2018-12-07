package com.tilly.steven.stlbusarrivals

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.*

object Utils {

    @JvmStatic
    fun toast(message: String, ctx: Context = MyApplication.context) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
    }
}

fun launchUI(block: suspend CoroutineScope.() -> Unit): Job {
    return CoroutineScope(Dispatchers.Main).launch {
        try {
            block()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}

suspend fun <T> asyncAwait(block: suspend CoroutineScope.() -> T): T {
    return CoroutineScope(Dispatchers.IO).async {
        block()
    }.await()
}
