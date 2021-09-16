package com.devapp.appforarduino.domain.usecases

import com.devapp.appforarduino.data.model.ImageData
import com.devapp.appforarduino.domain.app_repository.AppRepository
import kotlinx.coroutines.flow.Flow

class GetAllImageUseCase(private val appRepository: AppRepository){
    fun execute(): Flow<List<ImageData>> = appRepository.getAllImage()
}