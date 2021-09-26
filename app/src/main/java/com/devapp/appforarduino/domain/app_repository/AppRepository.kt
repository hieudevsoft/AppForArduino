package com.devapp.appforarduino.domain.app_repository

import android.graphics.Bitmap
import com.devapp.appforarduino.data.model.ImageData
import com.devapp.appforarduino.data.model.PixelData
import com.devapp.appforarduino.data.model.PixelDataTable
import com.devapp.appforarduino.data.model.TextData
import kotlinx.coroutines.flow.Flow


interface AppRepository {

    //Operation Firebase
    suspend fun updateTextAndColorToFirebase(textData: TextData)
    suspend fun updateOption(option:Int)
    suspend fun updateLaunchPad(array:List<PixelData>)
    suspend fun updateImage(bitmap: Bitmap)

    //Operation Local Data Source

    //Text
    suspend fun saveTextAndColor(textData: TextData):Long
    suspend fun deleteTextAndColor(textData: TextData):Int
    suspend fun deleteAllTextAndColor()
    fun getAllTextAndColor(): Flow<List<TextData>>
    fun getSearchedTextAndColor(query:String):Flow<List<TextData>>

    //LaunchPad
    suspend fun saveLaunchPad(pixelDataTable: PixelDataTable):Long
    suspend fun deleteLaunchPad(pixelDataTable: PixelDataTable):Int
    fun getAllLaunchPad(): Flow<List<PixelDataTable>>
    suspend fun deleteAllLaunchPad()
    fun getSearchedLaunchPad(query:String): Flow<List<PixelDataTable>>
    suspend fun checkIfExists(name:String):Int

    //Image
    suspend fun saveImage(imageData: ImageData):Long
    suspend fun deleteImage(imageData: ImageData):Int
    fun getAllImage(): Flow<List<ImageData>>
    suspend fun deleteAllImage()
}