package com.devapp.appforarduino.domain.usecases

import com.devapp.appforarduino.data.model.ImageData
import com.devapp.appforarduino.domain.app_repository.AppRepository

class DeleteImageUseCase(private val appRepository: AppRepository){
    suspend fun execute(imageData: ImageData):Int = appRepository.deleteImage(imageData)
}