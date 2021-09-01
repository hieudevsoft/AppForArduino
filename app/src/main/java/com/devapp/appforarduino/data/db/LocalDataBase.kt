package com.devapp.appforarduino.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.devapp.appforarduino.data.model.TextData
import com.devapp.appforarduino.util.Util

@Database(entities = [TextData::class], version = 1, exportSchema = false)
abstract class LocalDataBase() : RoomDatabase() {

    abstract fun getDao(): LocalDataService

    companion object {
        @Volatile
        private var INSTANCE: LocalDataBase? = null
        operator fun invoke(context: Context): LocalDataBase = if (INSTANCE != null) {
            INSTANCE as LocalDataBase
        } else {
            synchronized(this) {
                return Room.databaseBuilder(context, LocalDataBase::class.java, Util.NAME_DB)
                    .fallbackToDestructiveMigration()
                    .build()

            }
        }
    }
}