package com.devapp.appforarduino.domain.firebase_data_source

import com.devapp.appforarduino.data.model.TextData
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class FireBaseService(private val database:FirebaseDatabase){
    suspend fun updateTextAndColorToFirebase(textData: TextData){
        val myRef = database.reference.child("data")
        myRef.updateChildren(textData.toMap()).await()
    }
}