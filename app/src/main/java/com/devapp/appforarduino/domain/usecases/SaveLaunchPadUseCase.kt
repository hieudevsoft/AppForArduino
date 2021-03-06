package com.devapp.appforarduino.domain.usecases

import com.devapp.appforarduino.data.model.PixelDataTable
import com.devapp.appforarduino.data.model.TextData
import com.devapp.appforarduino.domain.app_repository.AppRepository

class SaveLaunchPadUseCase(private val appRepository: AppRepository){
    suspend fun execute(pixelDataTable: PixelDataTable):Long = appRepository.saveLaunchPad(pixelDataTable)
}