package com.devapp.appforarduino.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.devapp.appforarduino.R
import com.devapp.appforarduino.data.db.LocalDataBase
import com.devapp.appforarduino.data.model.TextData
import com.devapp.appforarduino.databinding.ActivityHomeBinding
import com.devapp.appforarduino.domain.app_repository.AppRepository
import com.devapp.appforarduino.domain.app_repository.AppRepositoryImpl
import com.devapp.appforarduino.domain.firebase_data_source.FireBaseRepository
import com.devapp.appforarduino.domain.firebase_data_source.FireBaseRepositoryImpl
import com.devapp.appforarduino.domain.firebase_data_source.FireBaseService
import com.devapp.appforarduino.domain.local_data_source.LocalDataRepository
import com.devapp.appforarduino.domain.local_data_source.LocalDataRepositoryImpl
import com.devapp.appforarduino.domain.usecases.UpdateTextAndColorToFirebaseUseCase
import com.devapp.appforarduino.ui.viewmodels.HomeViewModel
import com.devapp.appforarduino.ui.viewmodels.HomeViewModelFactory
import com.devapp.appforarduino.util.Util
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var firebaseService: FireBaseService
    private lateinit var firebaseRepository: FireBaseRepository
    private lateinit var localDataBase:LocalDataBase
    private lateinit var localDataRepository:LocalDataRepository
    private lateinit var appRepository: AppRepository
    private lateinit var updateTextAndColorToFirebaseUseCase: UpdateTextAndColorToFirebaseUseCase
    private lateinit var homeViewModel: HomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupInitUpdateTextAndColor()
        //homeViewModel.updateTextAndColorToFireBase(TextData("Chao"))
        subscriberObservers()
    }

    private fun subscriberObservers() {

    }

    private fun setupInitUpdateTextAndColor() {
        firebaseService = FireBaseService(FirebaseDatabase.getInstance())
        firebaseRepository = FireBaseRepositoryImpl(firebaseService)
        localDataBase = LocalDataBase(this)
        localDataRepository = LocalDataRepositoryImpl(localDataBase.getDao())
        appRepository = AppRepositoryImpl(firebaseRepository,localDataRepository)
        updateTextAndColorToFirebaseUseCase = UpdateTextAndColorToFirebaseUseCase(appRepository)
//        homeViewModel = ViewModelProvider(
//            this,
//            HomeViewModelFactory(
//                application,
//                updateTextAndColorToFirebaseUseCase
//            )
//        ).get(HomeViewModel::class.java)
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }
}