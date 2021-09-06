package com.devapp.appforarduino.data.model

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.devapp.appforarduino.util.Util

@Entity(tableName = Util.TABLE_PIXEL_AND_COLOR)
class PixelDataTable {
    @PrimaryKey
    lateinit var id:String

    lateinit var data:List<PixelData>
}