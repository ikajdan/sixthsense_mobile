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

        binding.colorPickerView.setColor(0)
        binding.colorPickerView.setColorListener {
            i, s -> binding.ledColorInput.setText(s.substring(1).uppercase())
        }

        binding.requestButton.setOnClickListener {
            setLedGrid()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

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
