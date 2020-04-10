package com.davidsadler.bluepail.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class PlantListFilterViewModel(application: Application): AndroidViewModel(application) {

    private var selectedColor: Int = 0

    fun updateSelectedColor(colorId: Int) {
        selectedColor = colorId
    }

    fun getSelectedColorId(): Int {
        return selectedColor
    }
}