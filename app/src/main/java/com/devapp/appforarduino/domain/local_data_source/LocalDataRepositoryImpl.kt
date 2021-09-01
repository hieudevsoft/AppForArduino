package com.devapp.appforarduino.domain.local_data_source

import com.devapp.appforarduino.data.db.LocalDataService
import com.devapp.appforarduino.data.model.TextData
import kotlinx.coroutines.flow.Flow

class LocalDataRepositoryImpl(private val dao:LocalDataService):LocalDataRepository {
    override suspend fun saveTextAndColor(textData: TextData): Long {
        return dao.saveTextAndColor(textData)
    }

    override suspend fun deleteTextAndColor(textData: TextData): Int {
        return dao.deleteTextAndColor(textData)
    }

    override fun getAllTextAndColor(): Flow<List<TextData>> {
        return dao.getAllTextAndColor()
    }

    override suspend fun deleteAllTextAndColor() {
        return dao.deleteAllTextAndColor()
    }

    override fun getSearchedTextAndColor(query: String): Flow<List<TextData>> {
        return dao.getSearchedTextAndColor(query)
    }
}