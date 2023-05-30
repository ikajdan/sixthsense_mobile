package io.github.ikajdan.sixthsense.ui.control

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel class for the ControlFragment.
 * Manages the data related to the control functionality.
 */
class ControlViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Query Response"
    }

    /**
     * LiveData object representing the text to be displayed in the ControlFragment.
     */
    val text: LiveData<String> = _text
}
