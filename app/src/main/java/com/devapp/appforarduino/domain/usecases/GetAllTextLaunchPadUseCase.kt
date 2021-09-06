package com.devapp.appforarduino.domain.usecases

import com.devapp.appforarduino.data.model.PixelData
import com.devapp.appforarduino.data.model.PixelDataTable
import com.devapp.appforarduino.data.model.TextData
import com.devapp.appforarduino.domain.app_repository.AppRepository
import kotlinx.coroutines.flow.Flow

class GetAllTextLaunchPadUseCase(private val appRepository: AppRepository){
    fun execute(): Flow<List<PixelDataTable>> = appRepository.getAllLaunchPad()
}