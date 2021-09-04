package com.devapp.appforarduino.domain.usecases

import com.devapp.appforarduino.data.model.PixelData
import com.devapp.appforarduino.data.model.TextData
import com.devapp.appforarduino.domain.app_repository.AppRepository

class UpdatePixelDataUseCase(private val appRepository: AppRepository){
    suspend fun execute(list: List<PixelData>) = appRepository.updatePixelData(list)
}