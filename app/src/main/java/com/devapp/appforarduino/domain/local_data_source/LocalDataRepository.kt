package com.devapp.appforarduino.domain.local_data_source

import com.devapp.appforarduino.data.model.TextData
import kotlinx.coroutines.flow.Flow

interface LocalDataRepository {
    suspend fun saveTextAndColor(textData: TextData):Long
    suspend fun deleteTextAndColor(textData: TextData):Int
    fun getAllTextAndColor(): Flow<List<TextData>>
    suspend fun deleteAllTextAndColor()
    fun getSearchedTextAndColor(query:String): Flow<List<TextData>>
}