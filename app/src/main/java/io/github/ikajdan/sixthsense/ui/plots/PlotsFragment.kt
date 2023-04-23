package io.github.ikajdan.sixthsense.ui.plots

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartAnimationType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import io.github.ikajdan.sixthsense.databinding.FragmentPlotsBinding

class PlotsFragment : Fragment() {

    private var _binding: FragmentPlotsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlotsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val aaChartView = binding.aaChartView
        val aaChartModel : AAChartModel = AAChartModel()
            .chartType(AAChartType.Line)
            .backgroundColor("#00000000")
            .colorsTheme(arrayOf("#b41c56"))
            .yAxisGridLineWidth(0)
            .animationDuration(0)
            .markerRadius(3)
            .series(arrayOf(
                AASeriesElement()
                    .name("Tokyo")
                    .data(arrayOf(7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6))
            )
        )
        aaChartView.aa_drawChartWithChartModel(aaChartModel)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}