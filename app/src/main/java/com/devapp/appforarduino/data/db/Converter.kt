package com.devapp.appforarduino.data.db

import androidx.room.TypeConverter
import com.devapp.appforarduino.data.model.PixelData
import com.devapp.appforarduino.data.model.PixelDataTable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converter {

    @TypeConverter
    fun fromListPixelData(list: List<PixelData>) = Gson().toJson(list)

    @TypeConverter
    fun toPixelData(json:String) = Gson().fromJson<List<PixelData>>(json,object:TypeToken<List<PixelData>>(){}.type)

}