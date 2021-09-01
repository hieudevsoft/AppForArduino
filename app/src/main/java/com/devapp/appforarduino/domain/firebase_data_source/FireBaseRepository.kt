package com.devapp.appforarduino.domain.firebase_data_source

import com.devapp.appforarduino.data.model.TextData

interface FireBaseRepository {
    suspend fun updateTextAndColorToFirebase(textData: TextData)
}