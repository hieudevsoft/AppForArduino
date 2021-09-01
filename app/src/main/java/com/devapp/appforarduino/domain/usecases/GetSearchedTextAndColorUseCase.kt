package com.devapp.appforarduino.domain.usecases

import com.devapp.appforarduino.data.model.TextData
import com.devapp.appforarduino.domain.app_repository.AppRepository
import kotlinx.coroutines.flow.Flow

class GetSearchedTextAndColorUseCase(private val appRepository: AppRepository){
    fun execute(query:String): Flow<List<TextData>> = appRepository.getSearchedTextAndColor(query)
}