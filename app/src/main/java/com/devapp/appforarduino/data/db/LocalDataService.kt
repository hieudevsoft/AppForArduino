package com.devapp.appforarduino.data.db

import androidx.room.*
import com.devapp.appforarduino.data.model.PixelDataTable
import com.devapp.appforarduino.data.model.TextData
import com.devapp.appforarduino.util.Util
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalDataService {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTextAndColor(textData: TextData):Long

    @Delete
    suspend fun deleteTextAndColor(textData: TextData):Int

    @Query("SELECT * FROM ${Util.TABLE_TEXT_AND_COLOR} ")
    fun getAllTextAndColor(): Flow<List<TextData>>

    @Query("DELETE  FROM ${Util.TABLE_TEXT_AND_COLOR} ")
    suspend fun deleteAllTextAndColor()

    @Query("SELECT * FROM ${Util.TABLE_TEXT_AND_COLOR} WHERE text like '%'||:query||'%'")
    fun getSearchedTextAndColor(query:String): Flow<List<TextData>>



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLaunchPad(pixelDataTable: PixelDataTable):Long

    @Delete
    suspend fun deleteLaunchPad(pixelDataTable: PixelDataTable):Int

    @Query("SELECT * FROM ${Util.TABLE_PIXEL_AND_COLOR} ")
    fun getAllLaunchPad(): Flow<List<PixelDataTable>>

    @Query("DELETE  FROM ${Util.TABLE_PIXEL_AND_COLOR} ")
    suspend fun deleteAllLaunchPad()

    @Query("SELECT * FROM ${Util.TABLE_PIXEL_AND_COLOR} WHERE id like '%'||:query||'%'")
    fun getSearchedLaunchPad(query:String): Flow<List<PixelDataTable>>

    @Query("SELECT COUNT(*) FROM ${Util.TABLE_PIXEL_AND_COLOR} WHERE id=:name")
    suspend fun checkIfExists(name:String):Int
}