package com.devapp.appforarduino.domain.usecases

import android.graphics.Bitmap
import com.devapp.appforarduino.data.model.PixelData
import com.devapp.appforarduino.domain.app_repository.AppRepository

class UpdateImageUseCase(private val appRepository: AppRepository){
    suspend fun execute(bitmap: Bitmap) = appRepository.updateImage(bitmap)
}