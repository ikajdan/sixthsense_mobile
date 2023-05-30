package io.github.ikajdan.sixthsense.ui.sensors

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @brief ViewModel class for managing sensor data.
 */
class SensorsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Loading…"
    }

    /**
     * @brief LiveData object representing the sensor data text.
     */
    val text: LiveData<String> = _text
}
