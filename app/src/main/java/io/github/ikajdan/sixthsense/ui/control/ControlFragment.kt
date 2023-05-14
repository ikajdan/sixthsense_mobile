package io.github.ikajdan.sixthsense.ui.control

import android.R
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.madrapps.pikolo.ColorPicker
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener
import io.github.ikajdan.sixthsense.databinding.FragmentControlBinding


class ControlFragment : Fragment() {
    private var _binding: FragmentControlBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentControlBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.colorPicker.setColorSelectionListener(object : SimpleColorSelectionListener() {
            override fun onColorSelected(color: Int) {
                //binding.imageView.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
                binding.imageView.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                binding.ledColorInput.setText(String.format("%06X", 0xFFFFFF and color))
            }
        })

        binding.ledPosXInput.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                binding.ledPosXInput.setText("")
            }
        }

        binding.ledPosYInput.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                binding.ledPosYInput.setText("")
            }
        }

        binding.requestButton.setOnClickListener {
            requestRoutine()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Request routine
    private fun requestRoutine() {
        val host = "http://" + "laptop.lan" + "/leds/set/"
        val port = ":" + "8000"
        val reqX = "x=" + binding.ledPosXInput.text.toString()
        val reqY = "y=" + binding.ledPosYInput.text.toString()
        val hexColor =  binding.ledColorInput.text.toString()
        val reqR = "r=" + Integer.parseInt(hexColor.substring(0, 2), 16)
        val reqG = "g=" + Integer.parseInt(hexColor.substring(2, 4), 16)
        val reqB = "b=" + Integer.parseInt(hexColor.substring(4, 6), 16)

        var url = "$host$port/v1/set?$reqX&$reqY&$reqR&$reqG&$reqB".lowercase()

        if (url.isEmpty()) return

        // Prepend with the protocol, if necessary
        if (!url.startsWith("https://", true) && !url.startsWith("http://", true)) {
            url = "http://$url"
        }

        // Instantiate the RequestQueue
        val queue = Volley.newRequestQueue(activity)

        // Request a string response from the provided URL
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            {
            },
            {
                // Display the error message
                // val msg = if (it.localizedMessage.isNullOrEmpty()) "Unknown Error" else it.localizedMessage
                // Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)
                //     .setAnchorView(binding.requestButton).show()
            }
        )

        // Add the request to the RequestQueue
        queue.add(stringRequest)
    }
}