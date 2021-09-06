package com.devapp.appforarduino.domain.firebase_data_source

import com.devapp.appforarduino.data.model.PixelData
import com.devapp.appforarduino.data.model.PixelDataTable
import com.devapp.appforarduino.data.model.TextData
import com.devapp.appforarduino.util.Util
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class FireBaseService(private val database:FirebaseDatabase){
    suspend fun updateTextAndColorToFirebase(textData: TextData){
        val myRef = database.reference.child("data")
        myRef.updateChildren(textData.toMap()).await()
    }
    suspend fun updateOption(option:Int){
        val myRef = database.reference.child("options")
        myRef.setValue(option).await()
    }
    suspend fun updateLaunchPad(list:List<PixelData>){
        val myRef = database.reference.child("launchpad")
        myRef.setValue(Util.covertListColorToRgbArray(list)).await()
    }
}