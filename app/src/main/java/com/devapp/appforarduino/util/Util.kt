package com.devapp.appforarduino.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Html
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.view.menu.MenuItemImpl
import com.devapp.appforarduino.R
import com.devapp.appforarduino.data.model.PixelData
import com.google.android.material.bottomnavigation.BottomNavigationView

object Util {
    const val TIME_OUT_CHECK_EXISTS = 1500L
    const val SEARCH_DEFAULT_DELAY = 500L
    const val EVENT_STATE_NOT_INTERNET_CONNECTTED = "No Internet Connected"
    const val TABLE_TEXT_AND_COLOR ="table_text_and_color"
    const val TABLE_PIXEL_AND_COLOR ="table_pixel_and_color"
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
            return when{
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else-> false
            }
        }else
        {
            val networkInfor = connectManager.activeNetworkInfo
            return networkInfor!=null && networkInfor.isConnected
        }
    }

    @SuppressLint("RestrictedApi")
    fun BottomNavigationView.deselectAllItems() {
        val menu = this.menu
        for(i in 0 until menu.size()) {
            (menu.getItem(i) as? MenuItemImpl)?.let {
                it.isExclusiveCheckable = false
                it.isChecked = false
                it.isExclusiveCheckable = true
            }
        }
    }

    fun validDataText(value:String) = value.trim().replace("\n","")

    private fun mapRGBToList(map: Map<String,Int>) = map.map { (_,value)->value }
    fun covertListColorToRgbArray(list:List<PixelData>) = list.map { mapRGBToList(convertHexToRgb(it.color!!)) }.flatMap { it.toList() }
}