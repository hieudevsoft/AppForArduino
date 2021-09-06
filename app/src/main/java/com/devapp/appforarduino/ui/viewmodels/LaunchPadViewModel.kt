package com.devapp.appforarduino.ui.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.devapp.appforarduino.data.model.PixelData
import com.devapp.appforarduino.data.model.PixelDataTable
import com.devapp.appforarduino.data.model.TextData
import com.devapp.appforarduino.domain.usecases.*
import com.devapp.appforarduino.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LaunchPadViewModel(
    private val app: Application,
    private val updateLaunchPadUseCase: UpdateLaunchPadUseCase?=null,
    private val updateOptionUseCase: UpdateOptionUseCase?=null,
    private val saveLaunchPadUseCase: SaveLaunchPadUseCase?=null,
    private val deleteLaunchPadUseCase: UpdateLaunchPadUseCase?=null,
    private val deleteAllLaunchPadUseCase: DeleteAllLaunchPadUseCase?=null,
    private val getAllTextLaunchPadUseCase: GetAllTextLaunchPadUseCase?=null,
    private val getSearchedLaunchPadUseCase: GetSearchedLaunchPadUseCase?=null
) : AndroidViewModel(app) {

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Empty)
    val updateState: StateFlow<UpdateState> = _updateState

    fun updateOptionToFireBase(option: Int) {
        safeUpdateOptionToFireBase(option)
    }

    fun saveLaunchPad(pixelDataTable: PixelDataTable) {
        safeSaveLaunchPad(pixelDataTable)
    }
    private fun safeUpdateOptionToFireBase(option: Int) {
        if (Util.checkInternetAvailable(app)) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    updateOptionUseCase?.execute(option)
                } catch (e: Exception) {
                }
            }
        }
    }
    private fun safeSaveLaunchPad(pixelDataTable: PixelDataTable) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (saveLaunchPadUseCase != null) {
                    async { saveLaunchPadUseCase.execute(pixelDataTable) }.await()
                }
            } catch (e: Exception) {
            }
        }
    }


    fun updateLaunchPadToFireBase(list: List<PixelData>) {
        safeUpdateLaunchPadToFireBase(list)
    }
    private fun safeUpdateLaunchPadToFireBase(list: List<PixelData>) {
        if (Util.checkInternetAvailable(app)) {
            viewModelScope.launch(Dispatchers.IO) {
                _updateState.value = UpdateState.Loading
                try {
                    updateLaunchPadUseCase?.execute(list)
                    _updateState.value = UpdateState.Success
                } catch (e: Exception) {
                    _updateState.value = UpdateState.Error(e.message!!)
                }
            }
        } else _updateState.value = UpdateState.Error(Util.EVENT_STATE_NOT_INTERNET_CONNECTTED)
    }
    sealed class UpdateState {
        object Success : UpdateState()
        data class Error(val message: String) : UpdateState()
        object Loading : UpdateState()
        object Empty : UpdateState()
    }
}

class LaunchPadViewModelFactory(
    private val app: Application,
    private val updateLaunchPadUseCase: UpdateLaunchPadUseCase?=null,
    private val updateOptionUseCase: UpdateOptionUseCase?=null,
    private val saveLaunchPadUseCase: SaveLaunchPadUseCase?=null,
    private val deleteLaunchPadUseCase: UpdateLaunchPadUseCase?=null,
    private val deleteAllLaunchPadUseCase: DeleteAllLaunchPadUseCase?=null,
    private val getAllTextLaunchPadUseCase: GetAllTextLaunchPadUseCase?=null,
    private val getSearchedLaunchPadUseCase: GetSearchedLaunchPadUseCase?=null
) : ViewModelProvider.AndroidViewModelFactory(app) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LaunchPadViewModel::class.java)) return LaunchPadViewModel(
            app,
            updateLaunchPadUseCase,
            updateOptionUseCase,
            saveLaunchPadUseCase,
            deleteLaunchPadUseCase,
            deleteAllLaunchPadUseCase,
            getAllTextLaunchPadUseCase,
            getSearchedLaunchPadUseCase
        ) as T else throw IllegalArgumentException("Not found LaunchPadViewModel")
    }
}