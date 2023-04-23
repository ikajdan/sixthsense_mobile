package io.github.ikajdan.sixthsense.ui.control

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import io.github.ikajdan.sixthsense.databinding.FragmentControlBinding

class ControlFragment : Fragment() {

    private var _binding: FragmentControlBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Request routine
    private fun requestRoutine() {
        var host = "http://" + "10.0.2.2"
        val port = ":" + "8080"
        val reqX = "x=" + binding.ledPosXInput.text.toString()
        val reqY = "y=" + binding.ledPosYInput.text.toString()
        val reqColor = "c=" + binding.ledColorInput.text.toString()

        var url = "$host$port/index.php?$reqX&$reqY&$reqColor".lowercase()

        if (url.isEmpty()) return

        binding.textControl.text = ""

        // Prepend with the protocol, if necessary
        if (!url.startsWith("https://", true) && !url.startsWith("http://", true)) {
            url = "http://$url"
        }

        // Instantiate the RequestQueue
        val queue = Volley.newRequestQueue(activity)

        // Request a string response from the provided URL
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                // Show the response
                binding.textControl.text = response
            },
            {
                // Display the error message
                val msg = if (it.localizedMessage.isNullOrEmpty()) "Unknown Error" else it.localizedMessage
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)
                    .setAnchorView(binding.requestButton).show()
            }
        )

        // Add the request to the RequestQueue
        queue.add(stringRequest)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val controlViewModel =
            ViewModelProvider(this).get(ControlViewModel::class.java)

        _binding = FragmentControlBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textControl
        controlViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
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
}