package com.devapp.appforarduino.ui.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.devapp.appforarduino.data.model.PixelData
import com.devapp.appforarduino.data.model.PixelDataTable
import com.devapp.appforarduino.domain.usecases.*
import com.devapp.appforarduino.util.Util
import com.devapp.appforarduino.util.Util.TIME_OUT_CHECK_EXISTS
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LaunchPadViewModel(
		private val app: Application,
		private val updateLaunchPadUseCase: UpdateLaunchPadUseCase? = null,
		private val updateOptionUseCase: UpdateOptionUseCase? = null,
		private val saveLaunchPadUseCase: SaveLaunchPadUseCase? = null,
		private val deleteLaunchPadUseCase: DeleteLaunchPadUseCase? = null,
		private val deleteAllLaunchPadUseCase: DeleteAllLaunchPadUseCase? = null,
		private val getAllTextLaunchPadUseCase: GetAllTextLaunchPadUseCase? = null,
		private val getSearchedLaunchPadUseCase: GetSearchedLaunchPadUseCase? = null,
		private val getCheckIfLaunchPadExists: CheckIfLaunchPadExists? = null
) : AndroidViewModel(app) {

		private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Empty)
		val updateState: StateFlow<UpdateState> = _updateState

		val deleteLaunchPadState = MutableLiveData<Boolean>()



		private val _deleteAllLaunchPadState = MutableLiveData<Boolean?>()
		fun setNullForDeleteAllTextAndColorState() = _deleteAllLaunchPadState.postValue(null)
		val deleteAllLaunchPadState: LiveData<Boolean?> = _deleteAllLaunchPadState

		val getAllLaunchPad = getAllTextLaunchPadUseCase?.execute()

		val currentQueryText = MutableLiveData<String>()

		@ExperimentalCoroutinesApi
		fun getSearchedLaunchPad(query: String) = getSearchedLaunchPadUseCase?.execute(query)

		fun updateOptionToFireBase(option: Int) {
				safeUpdateOptionToFireBase(option)
		}

		fun saveLaunchPad(pixelDataTable: PixelDataTable) {
				safeSaveLaunchPad(pixelDataTable)
		}

		fun deleteLaunchPad(pixelDataTable: PixelDataTable) {
				safeDeleteLaunchPad(pixelDataTable)
		}

		private fun safeDeleteLaunchPad(pixelDataTable: PixelDataTable) {
				viewModelScope.launch(Dispatchers.IO) {
						try {
								val result =
										withContext(Dispatchers.Default) {
												deleteLaunchPadUseCase?.execute(pixelDataTable)
										}
								if (result != null) {
										deleteLaunchPadState.postValue(result >= 1)
								}
						} catch (e: Exception) {
								deleteLaunchPadState.postValue(false)
						}
				}

		}

		fun deleteAllLaunchPad() {
				safeDeleteAllLaunchPad()
		}

		private fun safeDeleteAllLaunchPad() {
				viewModelScope.launch(Dispatchers.IO) {
						try {
								withContext(Dispatchers.Default) {
										if (deleteAllLaunchPadUseCase != null) {
												deleteAllLaunchPadUseCase.execute()
										}
								}
								_deleteAllLaunchPadState.postValue(true)
						} catch (e: Exception) {
								_deleteAllLaunchPadState.postValue(false)
						}
				}
		}

		private lateinit var result:Deferred<Int>
		suspend fun getCheckIfLaunchPadExists(id: String):Int {
				viewModelScope.launch(Dispatchers.IO) {
						coroutineScope {
								try {
										if (getCheckIfLaunchPadExists != null) {
												result = async{ getCheckIfLaunchPadExists.execute(id) }
										}
								} catch (e: Exception) {
								}
						}
				}
				delay(TIME_OUT_CHECK_EXISTS)
				return try {
						result.await()
				}catch (e:Exception){
						-1
				}
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
										async {
												saveLaunchPadUseCase.execute(
														pixelDataTable
												)
										}.await()
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
		private val updateLaunchPadUseCase: UpdateLaunchPadUseCase? = null,
		private val updateOptionUseCase: UpdateOptionUseCase? = null,
		private val saveLaunchPadUseCase: SaveLaunchPadUseCase? = null,
		private val deleteLaunchPadUseCase: DeleteLaunchPadUseCase? = null,
		private val deleteAllLaunchPadUseCase: DeleteAllLaunchPadUseCase? = null,
		private val getAllTextLaunchPadUseCase: GetAllTextLaunchPadUseCase? = null,
		private val getSearchedLaunchPadUseCase: GetSearchedLaunchPadUseCase? = null,
		private val getCheckIfLaunchPadExists: CheckIfLaunchPadExists? = null
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
						getSearchedLaunchPadUseCase,
						getCheckIfLaunchPadExists
				) as T else throw IllegalArgumentException("Not found LaunchPadViewModel")
		}
}