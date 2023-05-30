package io.github.ikajdan.sixthsense.ui.plots

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel class for the PlotsFragment.
 * Manages the data related to the plots functionality.
 */
class PlotsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Loading…"
    }

    /**
     * LiveData object representing the text to be displayed in the PlotsFragment.
     */
    val text: LiveData<String> = _text
}
