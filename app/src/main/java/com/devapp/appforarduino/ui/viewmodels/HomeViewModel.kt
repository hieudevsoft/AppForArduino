package com.devapp.appforarduino.ui.viewmodels


import android.app.Application
import androidx.lifecycle.*
import com.devapp.appforarduino.data.model.ImageData
import com.devapp.appforarduino.data.model.TextData
import com.devapp.appforarduino.domain.usecases.*
import com.devapp.appforarduino.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
		private val app: Application,
		private val updateTextAndColorToFirebaseUseCase: UpdateTextAndColorToFirebaseUseCase,
		private val saveTextAndColorUseCase: SaveTextAndColorUseCase,
		private val deleteTextAndColorUseCase: DeleteTextAndColorUseCase,
		private val deleteAllTextAndColorUseCase: DeleteAllTextAndColorUseCase,
		private val getAllTextAndColorUseCase: GetAllTextAndColorUseCase,
		private val getSearchedTextAndColorUseCase: GetSearchedTextAndColorUseCase,
		private val updateOptionUseCase: UpdateOptionUseCase,
		private val saveImageUseCase: SaveImageUseCase,
		private val deleteImageUseCase: DeleteImageUseCase,
		private val getAllImageUseCase: GetAllImageUseCase,
		private val deleteAllImageUseCase: DeleteAllImageUseCase
) : AndroidViewModel(app) {
		companion object {
				private val TAG = "HomeViewModel"
		}

		private val _stateTextEditText = MutableLiveData<HashMap<Int, String>>()
		val stateTextEditText = _stateTextEditText
		fun setStateText(haspMap: HashMap<Int, String>) = _stateTextEditText.postValue(haspMap)
		private val _stateImageAndPreview = MutableLiveData<HashMap<Int, Any?>>()
		val stateImageAndPreview = _stateImageAndPreview
		fun setStateImageAndPreview(map: HashMap<Int, Any?>) = _stateImageAndPreview.postValue(map)


		private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Empty)
		fun setEmptyForUpdateState() {
				_updateState.value = UpdateState.Empty
		}

		val updateState: StateFlow<UpdateState> = _updateState

		val deleteTextAndColorState = MutableLiveData<Boolean>()

		private val _deleteAllTextAndColorState = MutableLiveData<Boolean?>()
		fun setNullForDeleteAllTextAndColorState() = _deleteAllTextAndColorState.postValue(null)
		val deleteAllTextAndColorState: LiveData<Boolean?> = _deleteAllTextAndColorState

		val getAllTextAndColorData = getAllTextAndColorUseCase.execute()
		val getAllImage = getAllImageUseCase.execute()

		val currentQueryText = MutableLiveData<String>()

		@ExperimentalCoroutinesApi
		fun getSearchedTextAndColor(query: String) = getSearchedTextAndColorUseCase.execute(query)

		fun updateTextAndColorToFireBase(textData: TextData) {
				safeUpdateTextAndColorToFireBase(textData)
		}

		fun updateOptionToFireBase(option: Int) {
				safeUpdateOptionToFireBase(option)
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

		suspend fun saveImage(imageData: ImageData): Long {
				val result: Long
				try {
						result = saveImageUseCase.execute(imageData)
						return result
				} catch (e: Exception) {
						return -1
				}
		}

		fun deleteImage(imageData: ImageData) {
				viewModelScope.launch(Dispatchers.IO) {
						deleteImageUseCase.execute(imageData)
				}
		}

		fun deleteAllImage(){
				viewModelScope.launch(Dispatchers.IO){
						deleteAllImageUseCase.execute()
				}
		}

		private fun safeDeleteALlTextAndColor() {
				viewModelScope.launch(Dispatchers.IO) {
						try {
								withContext(Dispatchers.Default) { deleteAllTextAndColorUseCase.execute() }
								_deleteAllTextAndColorState.postValue(true)
						} catch (e: Exception) {
								_deleteAllTextAndColorState.postValue(false)
						}
				}
		}

		private fun safeDeleteTextAndColor(textData: TextData) {
				viewModelScope.launch(Dispatchers.IO) {
						try {
								val result =
										withContext(kotlinx.coroutines.Dispatchers.Default) {
												deleteTextAndColorUseCase.execute(textData)
										}
								deleteTextAndColorState.postValue(result >= 1)
						} catch (e: Exception) {
								deleteTextAndColorState.postValue(false)
						}
				}
		}


		private fun safeSaveTextAndColor(textData: TextData) {
				viewModelScope.launch(Dispatchers.IO) {
						try {
								withContext(Dispatchers.Default) {
										saveTextAndColorUseCase.execute(
												textData
										)
								}
						} catch (e: Exception) {
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

		private fun safeUpdateOptionToFireBase(option: Int) {
				if (Util.checkInternetAvailable(app)) {
						viewModelScope.launch(Dispatchers.IO) {
								try {
										updateOptionUseCase.execute(option)
								} catch (e: Exception) {
								}
						}
				}
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
		private val getSearchedTextAndColorUseCase: GetSearchedTextAndColorUseCase,
		private val updateOptionUseCase: UpdateOptionUseCase,
		private val saveImageUseCase: SaveImageUseCase,
		private val deleteImageUseCase: DeleteImageUseCase,
		private val getAllImageUseCase: GetAllImageUseCase,
		private val deleteAllImageUseCase: DeleteAllImageUseCase
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
								getSearchedTextAndColorUseCase,
								updateOptionUseCase,
								saveImageUseCase,
								deleteImageUseCase,
								getAllImageUseCase,
								deleteAllImageUseCase
						) as T
				else throw IllegalAccessException("Not Found HomeViewModel")
		}
}