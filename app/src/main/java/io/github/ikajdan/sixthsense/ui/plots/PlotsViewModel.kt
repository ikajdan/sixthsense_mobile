package io.github.ikajdan.sixthsense.ui.plots

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlotsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is plots Fragment"
    }
    val text: LiveData<String> = _text
}