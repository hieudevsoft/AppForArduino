package com.devapp.appforarduino.domain.firebase_data_source

import com.devapp.appforarduino.data.model.TextData

class FireBaseRepositoryImpl(private val firebaseService: FireBaseService):FireBaseRepository {
    override suspend fun updateTextAndColorToFirebase(textData: TextData) {
        return firebaseService.updateTextAndColorToFirebase(textData)
    }
}