package com.devapp.appforarduino.domain.local_data_source

import com.devapp.appforarduino.data.model.ImageData
import com.devapp.appforarduino.data.model.PixelDataTable
import com.devapp.appforarduino.data.model.TextData
import kotlinx.coroutines.flow.Flow

interface LocalDataRepository {

    //Method operation Text
    suspend fun saveTextAndColor(textData: TextData):Long
    suspend fun deleteTextAndColor(textData: TextData):Int
    fun getAllTextAndColor(): Flow<List<TextData>>
    suspend fun deleteAllTextAndColor()
    fun getSearchedTextAndColor(query:String): Flow<List<TextData>>

    //Method operation Launch Pad
    suspend fun saveLaunchPad(pixelDataTable: PixelDataTable):Long
    suspend fun deleteLaunchPad(pixelDataTable: PixelDataTable):Int
    fun getAllLaunchPad(): Flow<List<PixelDataTable>>
    suspend fun deleteAllLaunchPad()
    fun getSearchedLaunchPad(query:String): Flow<List<PixelDataTable>>
    suspend fun checkIfExists(name:String):Int

    //Method operation Image
    suspend fun saveImage(imageData: ImageData):Long
    suspend fun deleteImage(imageData: ImageData):Int
    fun getAllImage(): Flow<List<ImageData>>
    suspend fun deleteAllImage()

}