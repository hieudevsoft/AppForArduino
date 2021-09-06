package com.devapp.appforarduino.domain.usecases

import com.devapp.appforarduino.domain.app_repository.AppRepository

class CheckIfLaunchPadExists(private val appRepository: AppRepository){
    suspend fun execute(name:String) = appRepository.checkIfExists(name)
}