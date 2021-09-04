package com.devapp.appforarduino.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.devapp.appforarduino.data.model.TextData
import com.devapp.appforarduino.domain.usecases.*
import com.devapp.appforarduino.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest


import kotlinx.coroutines.launch

class HomeViewModel(
    private val app: Application,
    private val updateTextAndColorToFirebaseUseCase: UpdateTextAndColorToFirebaseUseCase,
    private val saveTextAndColorUseCase: SaveTextAndColorUseCase,
    private val deleteTextAndColorUseCase: DeleteTextAndColorUseCase,
    private val deleteAllTextAndColorUseCase: DeleteAllTextAndColorUseCase,
    private val getAllTextAndColorUseCase: GetAllTextAndColorUseCase,
    private val getSearchedTextAndColorUseCase: GetSearchedTextAndColorUseCase,
) : AndroidViewModel(app) {

    private val _stateTextEditText = MutableLiveData<HashMap<Int,String>>()
    val stateTextEditText = _stateTextEditText
    fun setStateText(haspMap:HashMap<Int,String>) = _stateTextEditText.postValue(haspMap)

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Empty)
    fun setEmptyForUpdateState() {
        _updateState.value=UpdateState.Empty
    }
    val updateState: StateFlow<UpdateState> = _updateState

    private val _saveTextAndColorState = MutableLiveData<Boolean>()
    val saveTextAndColorState: LiveData<Boolean> = _saveTextAndColorState

    val deleteTextAndColorState = MutableLiveData<Boolean>()


    private val _deleteAllTextAndColorState = MutableLiveData<Boolean>()
    fun setNullForDeleteAllTextAndColorState() = _deleteAllTextAndColorState.postValue(null)
    val deleteAllTextAndColorState: LiveData<Boolean> = _deleteAllTextAndColorState

    val getAllTextAndColorData = getAllTextAndColorUseCase.execute()

    val currentQueryText = MutableLiveData<String>()
    @ExperimentalCoroutinesApi
    fun getSearchedTextAndColor(query:String) = getSearchedTextAndColorUseCase.execute(query)

    fun updateTextAndColorToFireBase(textData: TextData) {
        safeUpdateTextAndColorToFireBase(textData)
    }

    fun saveTextAndColor(textData: TextData) {
        safeSaveTextAndColor(textData)
    }

    fun deleteTextAndColor(textData: TextData) {
        safeDeleteTextAndColor(textData)
    }

    fun deleteAllTextAndColor() {
        safeDeleteALlTextAndColor()
    }

    private fun safeDeleteALlTextAndColor() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                async { deleteAllTextAndColorUseCase.execute() }.await()
                _deleteAllTextAndColorState.postValue(true)
            } catch (e: Exception) {
                _deleteAllTextAndColorState.postValue(false)
            }
        }
    }

    private fun safeDeleteTextAndColor(textData: TextData) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = async { deleteTextAndColorUseCase.execute(textData) }.await()
                deleteTextAndColorState.postValue(result >= 1)
            } catch (e: Exception) {
                deleteTextAndColorState.postValue(false)
            }
        }
    }


    private fun safeSaveTextAndColor(textData: TextData) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = async { saveTextAndColorUseCase.execute(textData) }.await()
                _saveTextAndColorState.postValue(result >= 0)
            } catch (e: Exception) {
                _saveTextAndColorState.postValue(false)
            }
        }
    }

    private fun safeUpdateTextAndColorToFireBase(textData: TextData) {
        if (Util.checkInternetAvailable(app)) {
            viewModelScope.launch(Dispatchers.IO) {
                _updateState.value = UpdateState.Loading
                try {
                    updateTextAndColorToFirebaseUseCase.execute(textData)
                    _updateState.value = UpdateState.Success
                } catch (e: Exception) {
                    _updateState.value = UpdateState.Error(e.message!!)
                }
            }
        } else _updateState.value = UpdateState.Error(Util.EVENT_STATE_NOT_INTERNET_CONNECTTED)
    }

    companion object{
        private const val DEFAULT_SEARCH=""
    }

    sealed class UpdateState {
        object Success : UpdateState()
        data class Error(val message: String) : UpdateState()
        object Loading : UpdateState()
        object Empty : UpdateState()
    }
}

class HomeViewModelFactory(
    private val app: Application,
    private val updateTextAndColorToFirebaseUseCase: UpdateTextAndColorToFirebaseUseCase,
    private val saveTextAndColorUseCase: SaveTextAndColorUseCase,
    private val deleteTextAndColorUseCase: DeleteTextAndColorUseCase,
    private val deleteAllTextAndColorUseCase: DeleteAllTextAndColorUseCase,
    private val getAllTextAndColorUseCase: GetAllTextAndColorUseCase,
    private val getSearchedTextAndColorUseCase: GetSearchedTextAndColorUseCase
) : ViewModelProvider.AndroidViewModelFactory(app) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java))
            return HomeViewModel(
                app,
                updateTextAndColorToFirebaseUseCase,
                saveTextAndColorUseCase,
                deleteTextAndColorUseCase,
                deleteAllTextAndColorUseCase,
                getAllTextAndColorUseCase,
                getSearchedTextAndColorUseCase
            ) as T
        else throw IllegalAccessException("Not Found HomeViewModel")
    }
}