package io.github.ikajdan.sixthsense.ui.plots

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
        val plotsViewModel =
            ViewModelProvider(this).get(PlotsViewModel::class.java)

        _binding = FragmentPlotsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textPlots
        plotsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}