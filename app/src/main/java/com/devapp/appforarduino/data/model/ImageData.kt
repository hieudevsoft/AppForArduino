package com.devapp.appforarduino.data.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.devapp.appforarduino.util.Util
import java.io.Serializable

@Entity(tableName = Util.TABLE_IMAGE)
data class ImageData(
		var data: Bitmap
):Serializable{
		@PrimaryKey(autoGenerate = true)
		var id:Int?=null
}