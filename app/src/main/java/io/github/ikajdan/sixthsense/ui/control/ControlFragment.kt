package io.github.ikajdan.sixthsense.ui.control

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import io.github.ikajdan.sixthsense.databinding.FragmentControlBinding

/**
 * A fragment that displays the control functionality for LEDs.
 * Allows users to set LED colors using a color picker and send requests to update the LED grid.
 */
class ControlFragment : Fragment() {
    private var _binding: FragmentControlBinding? = null
    private val binding get() = _binding!!

    /**
     * Creates and returns the view hierarchy associated with the fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentControlBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.colorPickerView.setColor(0)
        binding.colorPickerView.setColorListener {
            i, s -> binding.ledColorInput.setText(s.substring(1).uppercase())
        }

        binding.requestButton.setOnClickListener {
            setLedGrid()
        }

        return root
    }

    /**
     * Called when the view previously created by onCreateView() has been detached from the fragment.
     * Clean up resources associated with the view.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Sets the LED grid based on the user-selected color.
     * Sends a request to the server to update the LED grid.
     */
    private fun setLedGrid() {
        binding.progressBar.visibility = View.VISIBLE

        val sharedPref = context?.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val hostNamePref = sharedPref?.getString("host_name", "laptop.lan").toString()
        val portNumberPref = sharedPref?.getInt("port_number", 8000).toString()
        val id = binding.ledIdInput.text.toString()
        val color =  binding.ledColorInput.text.toString().lowercase()
        val apiEndpoint = "http://$hostNamePref:$portNumberPref/leds/set/$id?hex=$color"
        val queue = Volley.newRequestQueue(activity)
        val stringRequest = StringRequest(
            Request.Method.GET, apiEndpoint,
            {
                binding.progressBar.visibility = View.INVISIBLE
            },
            {
            }
        )

        queue.add(stringRequest)
    }
}
