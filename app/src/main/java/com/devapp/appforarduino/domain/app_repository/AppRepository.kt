package com.devapp.appforarduino.domain.app_repository

import com.devapp.appforarduino.data.model.TextData
import kotlinx.coroutines.flow.Flow


interface AppRepository {
    suspend fun updateTextAndColorToFirebase(textData: TextData)
    suspend fun saveTextAndColor(textData: TextData):Long
    suspend fun deleteTextAndColor(textData: TextData):Int
    suspend fun deleteAllTextAndColor()
    fun getAllTextAndColor(): Flow<List<TextData>>
    fun getSearchedTextAndColor(query:String):Flow<List<TextData>>
}