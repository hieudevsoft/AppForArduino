package com.devapp.appforarduino.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.devapp.appforarduino.domain.usecases.UpdatePixelDataUseCase

class LaunchPadViewModel(
    app:Application,
    updatePixelDataUseCase: UpdatePixelDataUseCase
):AndroidViewModel(app) {



}