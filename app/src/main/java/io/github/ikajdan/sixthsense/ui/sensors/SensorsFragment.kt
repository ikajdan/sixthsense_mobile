package io.github.ikajdan.sixthsense.ui.sensors

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
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
        _binding = FragmentSensorsBinding.inflate(inflater, container, false)
        val root: View = binding.root

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
        val updateIntervalPref = 1000L
        // Start a timer to update the chart periodically
        mHandler.postDelayed(object : Runnable {
            override fun run() {
                updateSensorsData()
                mHandler.postDelayed(this, updateIntervalPref)
            }
        }, updateIntervalPref)
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
        val hostNamePref = "laptop.lan"
        val portNumberPref = "8000"
        val apiEndpoint =
            "http://$hostNamePref:$portNumberPref/sensors/all?t=c&p=hpa&h=perc&ro=deg&pi=deg&ya=deg"
        val requestQueue = Volley.newRequestQueue(context)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, apiEndpoint, null,
            { response ->
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