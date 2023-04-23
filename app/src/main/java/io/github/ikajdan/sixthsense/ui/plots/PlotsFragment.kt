package io.github.ikajdan.sixthsense.ui.plots

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import com.github.aachartmodel.aainfographics.aatools.AAGradientColor
import io.github.ikajdan.sixthsense.databinding.FragmentPlotsBinding
import kotlinx.coroutines.*
import kotlin.math.cos
import kotlin.math.sin

class PlotsFragment : Fragment() {
    private var _binding: FragmentPlotsBinding? = null
    private val binding get() = _binding!!

    private var aaChartModel = AAChartModel()
    private var aaChartView: AAChartView? = null

    private var temperature = arrayOfNulls<Any>(10)
    private var humidity = arrayOfNulls<Any>(10)
    private var pressure = arrayOfNulls<Any>(10)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlotsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setUpAAChartView()
        repeatUpdateChartData()

        return root
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }

    fun setUpAAChartView() {
        val aaChartView = binding.aaChartView
        aaChartModel = configureAAChartModel()
        aaChartView?.aa_drawChartWithChartModel(aaChartModel)
    }

    private fun configureAAChartModel(): AAChartModel {
        val aaChartModel : AAChartModel = AAChartModel()
            .chartType(AAChartType.Line)
            .backgroundColor("#00000000")
            .colorsTheme(arrayOf("#b41c56", "#da215e", "#ec8dad"))
            .yAxisGridLineWidth(0)
            .markerRadius(0)
            .xAxisVisible(false)
            .yAxisTitle("Temperature")
            .axesTextColor("#FFFFFF80")
            .animationType(AAChartAnimationType.EaseInOutQuint)
        aaChartModel.series(this.configureChartSeriesArray() as Array<Any>)
        return aaChartModel
    }

    @Suppress("UNCHECKED_CAST")
    private fun configureChartSeriesArray(): Array<AASeriesElement> {
        var min = 10
        var max = 25
        var random = (Math.random() * (max - min) + min).toInt()
        temperature += random
        if (temperature.size > 10) {
            temperature.drop(1)
        }

        min = 70
        max = 95
        random = (Math.random() * (max - min) + min).toInt()
        humidity += random
        if (humidity.size > 10) {
            humidity.drop(1)
        }

        min = 1000
        max = 1100
        random = (Math.random() * (max - min) + min).toInt()
        pressure += random
        if (pressure.size > 10) {
            pressure.drop(1)
        }

        return arrayOf(
            AASeriesElement()
                .name("Temperature")
                .data(temperature as Array<Any>),
            AASeriesElement()
                .name("Humidity")
                .data(humidity as Array<Any>),
            AASeriesElement()
                .name("Pressure")
                .data(pressure as Array<Any>),
        )
    }

    private fun repeatUpdateChartData() {
        val mStartVideoHandler = Handler()
        val mStartVideoRunnable: java.lang.Runnable = object : java.lang.Runnable {
            override fun run() {
                val seriesArr = configureChartSeriesArray()
                binding!!.aaChartView?.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(seriesArr)

                mStartVideoHandler.postDelayed(this, 1000)
            }
        }

        mStartVideoHandler.postDelayed(mStartVideoRunnable, 2000)
    }
}
