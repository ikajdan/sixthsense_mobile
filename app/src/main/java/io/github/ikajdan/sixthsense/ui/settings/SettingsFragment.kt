package io.github.ikajdan.sixthsense.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.github.ikajdan.sixthsense.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sharedPref = context?.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val hostNamePref = sharedPref?.getString("host_name", "server.lan")
        if (hostNamePref != null) {
            binding.hostInput.setText(hostNamePref.toString())
        }
        val portNumberPref = sharedPref?.getInt("port_number", 8000)
        if (portNumberPref != null) {
            binding.portInput.setText(portNumberPref.toString())
        }
        val updateIntervalPref = sharedPref?.getInt("sampling_time", 1000)
        if (updateIntervalPref != null) {
            binding.samplingTimeInput.setText(updateIntervalPref.toString())
        }

        binding.saveButton.setOnClickListener {
            val editor = sharedPref?.edit()
            if (editor != null) {
                editor.putString("host_name", binding.hostInput.text.toString())
                editor.putInt("port_number", binding.portInput.text.toString().toInt())
                editor.putInt("sampling_time", binding.samplingTimeInput.text.toString().toInt())
                editor.apply()
            }

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}