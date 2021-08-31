package com.devapp.appforarduino.util

import android.graphics.Color

object Util {
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

}