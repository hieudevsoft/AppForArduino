package com.devapp.appforarduino.domain.usecases

import com.devapp.appforarduino.data.model.TextData
import com.devapp.appforarduino.domain.app_repository.AppRepository

class UpdateTextAndColorToFirebaseUseCase(private val appRepository: AppRepository){
    suspend fun execute(textData: TextData) = appRepository.updateTextAndColorToFirebase(textData)
}