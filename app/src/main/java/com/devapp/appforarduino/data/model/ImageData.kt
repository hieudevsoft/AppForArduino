package com.devapp.appforarduino.data.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.devapp.appforarduino.util.Util

@Entity(tableName = Util.TABLE_IMAGE)
data class ImageData(
		var data: Bitmap
){
		@PrimaryKey(autoGenerate = true)
		var id:Int?=null
}