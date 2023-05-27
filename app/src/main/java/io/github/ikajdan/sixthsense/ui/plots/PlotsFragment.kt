package io.github.ikajdan.sixthsense.ui.plots

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.*
import com.github.dhaval2404.colorpicker.util.setVisibility
import io.github.ikajdan.sixthsense.databinding.FragmentPlotsBinding
import kotlinx.coroutines.*

class PlotsFragment : Fragment() {
    private var _binding: FragmentPlotsBinding? = null
    private val binding get() = _binding!!

    private var aaChartModel = AAChartModel()
    private var temperatureAA = arrayOfNulls<Any>(10)
    private var humidityAA = arrayOfNulls<Any>(10)
    private var pressureAA = arrayOfNulls<Any>(10)

    private val mHandler = Handler(Looper.getMainLooper())
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val plotsViewModel = ViewModelProvider(this).get(PlotsViewModel::class.java)

        _binding = FragmentPlotsBinding.inflate(inflater, container, false)

        val textView: TextView = binding.loadingPlaceholder
        plotsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        binding.progressBar.visibility = View.VISIBLE

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mHandler.removeCallbacksAndMessages(null)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpAAChartView()
    }

    private fun startUpdateTimer() {
        val sharedPref = context?.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val hostNamePref = sharedPref?.getString("host_name", "laptop.lan").toString()
        val portNumberPref = sharedPref?.getInt("port_number", 8000).toString()
        val apiEndpoint = "http://$hostNamePref:$portNumberPref/sensors/all?t=c&p=hpa&h=perc"
        var updateIntervalPref = sharedPref?.getInt("sampling_time", 1000)

        // Start a timer to update the chart periodically
        if (updateIntervalPref != null) {
            mHandler.postDelayed(object : Runnable {
                override fun run() {
                    updateSensorsPlot(apiEndpoint,
                        { temperature, pressure, humidity ->
                            temperatureAA += temperature.toFloat()
                            if (temperatureAA.size > 10) {
                                temperatureAA = temperatureAA.takeLast(10).toTypedArray()
                            }
                            humidityAA += humidity.toFloat()
                            if (humidityAA.size > 10) {
                                humidityAA = humidityAA.takeLast(10).toTypedArray()
                            }
                            pressureAA += pressure.toFloat()
                            if (pressureAA.size > 10) {
                                pressureAA = pressureAA.takeLast(10).toTypedArray()
                            }

                            val seriesArr = configureChartSeriesArray()

                            binding.aaChartView.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(seriesArr)
                        },
                        {
                        }
                    )
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

    private fun setUpAAChartView() {
        val aaChartView = binding.aaChartView
        aaChartModel = configureAAChartModel()

        val aaOptions: AAOptions = aaChartModel.aa_toAAOptions()

        aaOptions.yAxis(AAYAxis()
            .gridLineColor("#FFFFFF20")
            .title(AATitle()
                .text(aaChartModel.yAxisTitle)
            )
        )
            .tooltip(AATooltip()
            .enabled(true)
            .style(AAStyle()
                .color("#ffffff")
            )
            .backgroundColor("#4a4458")
            .borderColor("#e8def8")
            .borderRadius(10f)
            .borderWidth(1f)
            .headerFormat("")
            .valueDecimals(2)
            .shared(true)
        )

        aaChartView?.aa_drawChartWithChartOptions(aaOptions)
    }

    @Suppress("UNCHECKED_CAST")
    private fun configureAAChartModel(): AAChartModel {
        val aaChartModel : AAChartModel = AAChartModel()
            .chartType(AAChartType.Line)
            .backgroundColor("#00000000")
            .yAxisGridLineWidth(1)
            .xAxisVisible(false)
            .yAxisTitle("")
            .axesTextColor("#FFFFFF80")
        aaChartModel.series(this.configureChartSeriesArray() as Array<Any>)

        return aaChartModel
    }

    @Suppress("UNCHECKED_CAST")
    private fun configureChartSeriesArray(): Array<AASeriesElement> {
        return arrayOf(
            AASeriesElement()
                .name("Temperature [°C]")
                .data(temperatureAA as Array<Any>)
                .marker(
                    AAMarker()
                        .radius(3f)
                        .fillColor("#f66151")
                        .lineWidth(2f)
                        .lineColor("#f66151")
                        .symbol(AAChartSymbolType.Circle.value)
                ),
            AASeriesElement()
                .name("Pressure [hPa]")
                .data(pressureAA as Array<Any>)
                .marker(
                    AAMarker()
                        .radius(3f)
                        .fillColor("#f8e45c")
                        .lineWidth(2f)
                        .lineColor("#f8e45c")
                        .symbol(AAChartSymbolType.Circle.value)
                ),
            AASeriesElement()
                .name("Humidity [%]")
                .data(humidityAA as Array<Any>)
                .marker(
                    AAMarker()
                        .radius(3f)
                        .fillColor("#62a0ea")
                        .lineWidth(2f)
                        .lineColor("#62a0ea")
                        .symbol(AAChartSymbolType.Circle.value)
                )
        )
    }

    private fun updateSensorsPlot(url: String, successListener: (Double, Double, Double) -> Unit, errorListener: (Exception) -> Unit) {
        val requestQueue = Volley.newRequestQueue(context)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                binding.progressBar.visibility = View.INVISIBLE
                binding.loadingPlaceholder.setVisibility(false)

                val temperature = response.getJSONObject("temperature").getDouble("value")
                val pressure = response.getJSONObject("pressure").getDouble("value")
                val humidity = response.getJSONObject("humidity").getDouble("value")
                successListener(temperature, pressure, humidity)
            },
            { error ->
                errorListener(error)
            })

        requestQueue.add(jsonObjectRequest)
    }
}
