package com.devapp.appforarduino.domain.usecases

import com.devapp.appforarduino.data.model.TextData
import com.devapp.appforarduino.domain.app_repository.AppRepository

class UpdateOptionUseCase(private val appRepository: AppRepository){
    suspend fun execute(option: Int) = appRepository.updateOption(option)
}