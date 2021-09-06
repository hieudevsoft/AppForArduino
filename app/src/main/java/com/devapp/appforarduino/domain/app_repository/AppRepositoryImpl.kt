package com.devapp.appforarduino.domain.app_repository

import com.devapp.appforarduino.data.model.PixelData
import com.devapp.appforarduino.data.model.PixelDataTable
import com.devapp.appforarduino.data.model.TextData
import com.devapp.appforarduino.domain.firebase_data_source.FireBaseRepository
import com.devapp.appforarduino.domain.local_data_source.LocalDataRepository
import kotlinx.coroutines.flow.Flow

class AppRepositoryImpl(
    private val fireBaseRepository: FireBaseRepository,
    private val localDataRepository: LocalDataRepository
):AppRepository {
    override suspend fun updateTextAndColorToFirebase(textData: TextData) {
        fireBaseRepository.updateTextAndColorToFirebase(textData)
    }

    override suspend fun updateOption(option: Int) {
        fireBaseRepository.updateOptions(option)
    }

    override suspend fun updateLaunchPad(array: List<PixelData>) {
        fireBaseRepository.updateLaunchPad(array)
    }

    override suspend fun saveTextAndColor(textData: TextData): Long {
        return localDataRepository.saveTextAndColor(textData)
    }

    override suspend fun deleteTextAndColor(textData: TextData): Int {
        return localDataRepository.deleteTextAndColor(textData)
    }

    override fun getAllTextAndColor(): Flow<List<TextData>> {
        return localDataRepository.getAllTextAndColor()
    }

    override suspend fun deleteAllTextAndColor() {
        return localDataRepository.deleteAllTextAndColor()
    }

    override fun getSearchedTextAndColor(query: String): Flow<List<TextData>> {
        return localDataRepository.getSearchedTextAndColor(query)
    }

    override suspend fun saveLaunchPad(pixelDataTable: PixelDataTable): Long {
        return localDataRepository.saveLaunchPad(pixelDataTable)
    }

    override suspend fun deleteLaunchPad(pixelDataTable: PixelDataTable): Int {
        return localDataRepository.deleteLaunchPad(pixelDataTable)
    }

    override fun getAllLaunchPad(): Flow<List<PixelDataTable>> {
        return localDataRepository.getAllLaunchPad()
    }

    override suspend fun deleteAllLaunchPad() {
        localDataRepository.deleteAllLaunchPad()
    }

    override fun getSearchedLaunchPad(query: String): Flow<List<PixelDataTable>> {
        return localDataRepository.getSearchedLaunchPad(query)
    }

    override suspend fun checkIfExists(name: String): Int {
        return localDataRepository.checkIfExists(name)
    }
}