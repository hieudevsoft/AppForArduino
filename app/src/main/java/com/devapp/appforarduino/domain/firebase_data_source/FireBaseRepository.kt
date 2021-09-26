package com.devapp.appforarduino.domain.firebase_data_source

import android.graphics.Bitmap
import com.devapp.appforarduino.data.model.PixelData
import com.devapp.appforarduino.data.model.TextData

interface FireBaseRepository {
    suspend fun updateTextAndColorToFirebase(textData: TextData)
    suspend fun updateOptions(option:Int)
    suspend fun updateLaunchPad(list:List<PixelData>)
    suspend fun updateImage(bitmap: Bitmap)
}