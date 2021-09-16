package com.devapp.appforarduino.domain.usecases

import com.devapp.appforarduino.domain.app_repository.AppRepository

class DeleteAllImageUseCase(private val appRepository: AppRepository){
    suspend fun execute() = appRepository.deleteAllImage()
}