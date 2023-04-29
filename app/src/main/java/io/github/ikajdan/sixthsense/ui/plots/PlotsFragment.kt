package io.github.ikajdan.sixthsense.ui.plots

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.github.aachartmodel.aainfographics.aachartcreator.*
import io.github.ikajdan.sixthsense.databinding.FragmentPlotsBinding
import kotlinx.coroutines.*
import org.json.JSONObject

class PlotsFragment : Fragment() {
    private var _binding: FragmentPlotsBinding? = null
    private val binding get() = _binding!!

    private var aaChartView: AAChartView? = null
    private var aaChartModel = AAChartModel()
    private var temperatureAA = arrayOfNulls<Any>(10)
    private var humidityAA = arrayOfNulls<Any>(10)
    private var pressureAA = arrayOfNulls<Any>(10)

    private val mHandler = Handler(Looper.getMainLooper())
    private val mApiUrl = "http://10.0.2.2:8080/v1/get/?t=c&p=hpa&h=perc"
    private val mUpdateInterval = 1000L

    data class Temperature(val value: Double, val unit: String)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlotsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mHandler.removeCallbacksAndMessages(null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpAAChartView()
        updateChartData()
    }

    private fun startUpdateTimer() {
        // Start a timer to update the chart periodically
        mHandler.postDelayed(object : Runnable {
            override fun run() {
                fetchDataFromApi(mApiUrl,
                    { temperature, pressure, humidity ->
                        temperatureAA += temperature.toFloat()
//                        if (temperatureAA.size > 10) {
//                            temperatureAA = temperatureAA.takeLast(10).toTypedArray()
//                        }
                        humidityAA += humidity.toFloat()
//                        if (temperatureAA.size > 10) {
//                            temperatureAA = temperatureAA.takeLast(10).toTypedArray()
//                        }
                        pressureAA += pressure.toFloat()
//                        if (temperatureAA.size > 10) {
//                            temperatureAA = temperatureAA.takeLast(10).toTypedArray()
//                        }
                        updateChartData()
                    },
                    { error ->
                        // Error handler
                    }
                )
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

    private fun setUpAAChartView() {
        val aaChartView = binding.aaChartView
        aaChartModel = configureAAChartModel()
        aaChartView.aa_drawChartWithChartModel(aaChartModel)
    }

    @Suppress("UNCHECKED_CAST")
    private fun configureAAChartModel(): AAChartModel {
        val aaChartModel : AAChartModel = AAChartModel()
            .chartType(AAChartType.Line)
            .backgroundColor("#00000000")
            .colorsTheme(arrayOf("#b41c56", "#da215e", "#ec8dad"))
            .yAxisGridLineWidth(0)
            .markerRadius(0)
            .xAxisVisible(false)
            .yAxisTitle("Temperature (°C), Humidity (%), Pressure (hPa)")
            .axesTextColor("#FFFFFF80")
            .animationType(AAChartAnimationType.EaseInOutQuint)
        aaChartModel.series(this.configureChartSeriesArray() as Array<Any>)

        return aaChartModel
    }

    @Suppress("UNCHECKED_CAST")
    private fun configureChartSeriesArray(): Array<AASeriesElement> {
        return arrayOf(
            AASeriesElement()
                .name("Temperature")
                .data(temperatureAA as Array<Any>),
            AASeriesElement()
                .name("Humidity")
                .data(humidityAA as Array<Any>),
            AASeriesElement()
                .name("Pressure")
                .data(pressureAA as Array<Any>),
        )
    }

    private fun updateChartData() {
        val seriesArr = configureChartSeriesArray()
        binding.aaChartView.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(seriesArr)
    }

    private fun fetchDataFromApi(url: String, successListener: (Double, Double, Double) -> Unit, errorListener: (Exception) -> Unit) {
        val requestQueue = Volley.newRequestQueue(context)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener<JSONObject> { response ->
                val temperature = response.getJSONObject("temperature").getDouble("value")
                val pressure = response.getJSONObject("pressure").getDouble("value")
                val humidity = response.getJSONObject("humidity").getDouble("value")
                successListener(temperature, pressure, humidity)
            },
            Response.ErrorListener { error ->
                errorListener(error)
            })

        requestQueue.add(jsonObjectRequest)
    }
}
