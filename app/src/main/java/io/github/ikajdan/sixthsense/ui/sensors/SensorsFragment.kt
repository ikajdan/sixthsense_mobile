package io.github.ikajdan.sixthsense.ui.sensors

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import io.github.ikajdan.sixthsense.databinding.FragmentSensorsBinding

class SensorsFragment : Fragment() {
    private var _binding: FragmentSensorsBinding? = null
    private val binding get() = _binding!!

    private val mHandler = Handler(Looper.getMainLooper())
    private val mApiUrl = "http://10.0.2.2:8080/v1/get/?t=c&p=hpa&h=perc&ro=deg&pi=deg&ya=deg"
    private val mUpdateInterval = 1000L

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var data: MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val sensorsViewModel =
//            ViewModelProvider(this)[SensorsViewModel::class.java]

        _binding = FragmentSensorsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        data = mutableListOf()
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, data)
        this.binding.listView.adapter = adapter

        fetchDataFromApi(mApiUrl)

//        val textView: TextView = binding.textSensors
//        sensorsViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mHandler.removeCallbacksAndMessages(null)
    }

    private fun startUpdateTimer() {
        // Start a timer to update the chart periodically
        mHandler.postDelayed(object : Runnable {
            override fun run() {
                fetchDataFromApi(mApiUrl)
                mHandler.postDelayed(this, mUpdateInterval)
            }
        }, mUpdateInterval)
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

    private fun fetchDataFromApi(url: String) {
        val requestQueue = Volley.newRequestQueue(context)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                data.clear()
                data.add("Temperature: ${response.getJSONObject("temperature").getDouble("value")}" + " " +
                        response.getJSONObject("temperature").getString("unit")
                )
                data.add("Pressure: ${response.getJSONObject("pressure").getDouble("value")}" + " " +
                        response.getJSONObject("pressure").getString("unit")
                )
                data.add("Humidity: ${response.getJSONObject("humidity").getDouble("value")}" + " " +
                        response.getJSONObject("humidity").getString("unit")
                )
                data.add("Roll: ${response.getJSONObject("roll").getDouble("value")}" + " " +
                        response.getJSONObject("roll").getString("unit")
                )
                data.add("Pitch: ${response.getJSONObject("pitch").getDouble("value")}" + " " +
                        response.getJSONObject("pitch").getString("unit")
                )
                data.add("Yaw: ${response.getJSONObject("yaw").getDouble("value")}" + " " +
                        response.getJSONObject("yaw").getString("unit")
                )
                adapter.notifyDataSetChanged()
            },
            {
            })

        requestQueue.add(jsonObjectRequest)
    }
}