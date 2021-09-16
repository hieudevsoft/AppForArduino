package com.devapp.appforarduino.domain.local_data_source

import com.devapp.appforarduino.data.db.LocalDataService
import com.devapp.appforarduino.data.model.ImageData
import com.devapp.appforarduino.data.model.PixelDataTable
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

    override suspend fun saveLaunchPad(pixelDataTable: PixelDataTable): Long {
        return dao.saveLaunchPad(pixelDataTable)
    }

    override suspend fun deleteLaunchPad(pixelDataTable: PixelDataTable): Int {
        return dao.deleteLaunchPad(pixelDataTable)
    }

    override fun getAllLaunchPad(): Flow<List<PixelDataTable>> {
        return dao.getAllLaunchPad()
    }

    override suspend fun deleteAllLaunchPad() {
        dao.deleteAllLaunchPad()
    }

    override fun getSearchedLaunchPad(query: String): Flow<List<PixelDataTable>> {
        return dao.getSearchedLaunchPad(query)
    }

    override suspend fun checkIfExists(name: String): Int {
        return dao.checkIfExists(name)
    }

    override suspend fun saveImage(imageData: ImageData): Long {
        return dao.saveImage(imageData)
    }

    override suspend fun deleteImage(imageData: ImageData): Int {
        return dao.deleteImage(imageData)
    }

    override fun getAllImage(): Flow<List<ImageData>> {
        return dao.getAllImage()
    }

    override suspend fun deleteAllImage() {
        return dao.deleteAllImage()
    }
}