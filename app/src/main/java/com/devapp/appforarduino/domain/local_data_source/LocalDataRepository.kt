package com.devapp.appforarduino.domain.local_data_source

import com.devapp.appforarduino.data.model.PixelDataTable
import com.devapp.appforarduino.data.model.TextData
import kotlinx.coroutines.flow.Flow

interface LocalDataRepository {

    suspend fun saveTextAndColor(textData: TextData):Long
    suspend fun deleteTextAndColor(textData: TextData):Int
    fun getAllTextAndColor(): Flow<List<TextData>>
    suspend fun deleteAllTextAndColor()
    fun getSearchedTextAndColor(query:String): Flow<List<TextData>>

    suspend fun saveLaunchPad(pixelDataTable: PixelDataTable):Long
    suspend fun deleteLaunchPad(pixelDataTable: PixelDataTable):Int
    fun getAllLaunchPad(): Flow<List<PixelDataTable>>
    suspend fun deleteAllLaunchPad()
    fun getSearchedLaunchPad(query:String): Flow<List<PixelDataTable>>
    suspend fun checkIfExists(name:String):Int

}