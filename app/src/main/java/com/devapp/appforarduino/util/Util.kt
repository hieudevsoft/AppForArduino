package com.devapp.appforarduino.util

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Html
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.devapp.appforarduino.R
import java.lang.Exception

object Util {

    const val EVENT_STATE_NOT_INTERNET_CONNECTTED = "No Internet Connected"
    const val TABLE_TEXT_AND_COLOR ="table_text_and_color"
    const val NAME_DB ="db"
    const val SLOGAN_PAGE_1 = "Linh hoạt và tiện dụng \n" +
            "trong trang trí và quảng cáo"
    const val SLOGAN_PAGE_2 = "   Điều khiển thiết bị của \nbạn mọi lúc mọi nơi"
    const val SLOGAN_PAGE_3 = "  Đa dạng về cài đặt và hiển thị"

     fun addDot(context:Context,layout : LinearLayout,position:Int) {
         layout.removeAllViews()
         for ( i in 0 until 3) {
             val textView = TextView(context)
             textView.text = Html.fromHtml("&#8226;",Html.FROM_HTML_MODE_LEGACY)
             textView.textSize = 40f
             textView.setPadding(6,6,6,6)
             if(position==i)
             textView.setTextColor(context.resources.getColor(R.color.dot_selected,null))
             else textView.setTextColor(context.resources.getColor(R.color.dot_unselected,null))
             layout.addView(textView)
         }
     }
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