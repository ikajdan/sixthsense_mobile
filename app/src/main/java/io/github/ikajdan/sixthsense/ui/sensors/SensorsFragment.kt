package io.github.ikajdan.sixthsense.ui.sensors

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.github.dhaval2404.colorpicker.util.setVisibility
import io.github.ikajdan.sixthsense.databinding.FragmentSensorsBinding
import java.math.BigDecimal
import kotlin.math.round

class SensorsFragment : Fragment() {
    private var _binding: FragmentSensorsBinding? = null
    private val binding get() = _binding!!
    private val mHandler = Handler(Looper.getMainLooper())

    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var data: MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val sensorsViewModel = ViewModelProvider(this).get(SensorsViewModel::class.java)

        _binding = FragmentSensorsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.loadingPlaceholder
        sensorsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        binding.progressBar.visibility = View.VISIBLE

        data = mutableListOf()
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, data)
        this.binding.listView.adapter = adapter

        updateSensorsData()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mHandler.removeCallbacksAndMessages(null)
    }

    private fun startUpdateTimer() {
        val sharedPref = context?.getSharedPreferences("settings", Context.MODE_PRIVATE)
        var updateIntervalPref = sharedPref?.getInt("sampling_time", 1000)
        // Start a timer to update the chart periodically
        if (updateIntervalPref != null) {
            mHandler.postDelayed(object : Runnable {
                override fun run() {
                    updateSensorsData()
                    if (updateIntervalPref != null) {
                        mHandler.postDelayed(this, updateIntervalPref.toLong())
                    }
                }
            }, updateIntervalPref.toLong())
        }
    }

    private fun stopUpdateTimer() {
        mHandler.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        startUpdateTimer()
    }

    override fun onPause() {
        super.onPause()
        stopUpdateTimer()
    }

    private fun updateSensorsData() {
        val sharedPref = context?.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val hostNamePref = sharedPref?.getString("host_name", "laptop.lan").toString()
        val portNumberPref = sharedPref?.getInt("port_number", 8000).toString()
        val apiEndpoint =
            "http://$hostNamePref:$portNumberPref/sensors/all?t=c&p=hpa&h=perc&ro=deg&pi=deg&ya=deg"
        val requestQueue = Volley.newRequestQueue(context)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, apiEndpoint, null,
            { response ->
                binding.progressBar.visibility = View.INVISIBLE
                binding.loadingPlaceholder.setVisibility(false)

                data.clear()
                for (key in response.keys()) {
                    val innerObject = response.getJSONObject(key)
                    val name = innerObject.getString("name")
                    var value = innerObject.getDouble("value")
                    val unit = innerObject.getString("unit")

                    value = round(value * 100) / 100

                    // Trim trailing zeros
                    val bValue = BigDecimal.valueOf(value).stripTrailingZeros().toPlainString()

                    data.add("$name: $bValue$unit")
                }
                adapter.notifyDataSetChanged()
            },
            {
            })

        requestQueue.add(jsonObjectRequest)
    }
}