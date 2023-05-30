package io.github.ikajdan.sixthsense.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @brief ViewModel class for managing settings data.
 */
class SettingsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is settings Fragment"
    }

    /**
     * @brief LiveData object representing the settings text.
     */
    val text: LiveData<String> = _text
}
