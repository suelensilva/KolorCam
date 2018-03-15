package br.com.sooba.kolorcam.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import br.com.sooba.kolorcam.room.ColorCapture
import br.com.sooba.kolorcam.room.ColorCaptureRepository

/**
 * View Model of a captured color that manages live data
 */
class ColorCaptureViewModel(application: Application) : AndroidViewModel(application) {

    private var repository = ColorCaptureRepository(application)
    private var mAllCapturedColors : LiveData<List<ColorCapture>>

    init {
        mAllCapturedColors = repository.getAllCapturedColors()
    }

    fun getAllColors() : LiveData<List<ColorCapture>> {
        return mAllCapturedColors
    }

    fun insertColor(color : ColorCapture) {
        repository.insert(color)
    }
}