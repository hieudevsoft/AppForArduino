package com.devapp.appforarduino.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.devapp.appforarduino.util.Util

@Entity(tableName = Util.TABLE_TEXT_AND_COLOR)
data class TextData(var text:String="Hello I'm UwU",var color:String="#ffffff") {
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null

    fun toMap():Map<String,Any>{
        return hashMapOf(
            "text" to text,
            "color" to Util.convertHexToRgb(color)
        )
    }
}