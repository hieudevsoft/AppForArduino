package com.devapp.appforarduino.domain.firebase_data_source

import com.devapp.appforarduino.data.model.PixelData
import com.devapp.appforarduino.data.model.TextData

class FireBaseRepositoryImpl(private val firebaseService: FireBaseService):FireBaseRepository {
    override suspend fun updateTextAndColorToFirebase(textData: TextData) {
         firebaseService.updateTextAndColorToFirebase(textData)
    }

    override suspend fun updateOptions(option: Int) {
         firebaseService.updateOption(option)
    }

    override suspend fun updateLaunchPad(list: List<PixelData>) {
        firebaseService.updateLaunchPad(list)
    }

}