package com.devapp.appforarduino.util

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import java.lang.Exception

object Util {

    const val EVENT_STATE_NOT_INTERNET_CONNECTTED = "No Internet Connected"
    const val TABLE_TEXT_AND_COLOR ="table_text_and_color"
    const val NAME_DB ="db"

     fun convertHexToRgb(color: String):Map<String,Int>{
        val color = Color.parseColor(color)
        val red = color and 0xFF0000 shr 16
        val green = color and 0xFF00 shr 8
        val blue = color and 0xFF
        return hashMapOf(
            "red" to red,
            "green" to green,
            "blue" to blue
        )
    }

    fun checkInternetAvailable(context:Context):Boolean{
        val connectManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val activeNetwork = connectManager.activeNetwork
            val networkCapabilities = connectManager.getNetworkCapabilities(activeNetwork) ?: return false
            when{
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                else-> return false
            }
        }else
        {
            val networkInfor = connectManager.activeNetworkInfo
            return networkInfor!=null && networkInfor.isConnected
        }
    }

}