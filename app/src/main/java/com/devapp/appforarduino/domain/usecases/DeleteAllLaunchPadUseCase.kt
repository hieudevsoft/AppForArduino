package com.devapp.appforarduino.domain.usecases

import com.devapp.appforarduino.domain.app_repository.AppRepository

class DeleteAllLaunchPadUseCase(private val appRepository: AppRepository){
    suspend fun execute() = appRepository.deleteAllLaunchPad()
}