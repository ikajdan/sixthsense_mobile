package io.github.ikajdan.sixthsense.ui.sensors

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SensorsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Loading…"
    }
    val text: LiveData<String> = _text
}