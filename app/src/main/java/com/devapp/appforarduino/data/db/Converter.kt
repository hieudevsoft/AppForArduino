package com.devapp.appforarduino.data.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.devapp.appforarduino.data.model.PixelData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream

class Converter {

    @TypeConverter
    fun fromListPixelData(list: List<PixelData>) = Gson().toJson(list)!!

    @TypeConverter
    fun toPixelData(json:String) = Gson().fromJson<List<PixelData>>(json,object:TypeToken<List<PixelData>>(){}.type)!!

    @TypeConverter
    fun fromImageData(bitmap:Bitmap):ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray):Bitmap{
        return BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
    }

}