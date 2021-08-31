package com.devapp.appforarduino.data.model

import com.devapp.appforarduino.util.Util

data class TextData(var text:String="Hello I'm UwU",var color:String="#ffffff") {

    fun toMap():Map<String,Any>{
        return hashMapOf(
            "text" to text,
            "color" to Util.convertHexToRgb(color)
        )
    }
}