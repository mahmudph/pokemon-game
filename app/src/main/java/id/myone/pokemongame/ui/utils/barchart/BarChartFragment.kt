package id.myone.pokemongame.ui.utils.barchart

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import id.myone.pokemongame.R
import id.myone.pokemongame.common.availableColor
import id.myone.pokemongame.common.pokemonStateTypes
import id.myone.pokemongame.databinding.FragmentBarChartBinding
import id.myone.pokemongame.extensions.isDarkMode

class BarChartFragment : Fragment() {

    private lateinit var binding: FragmentBarChartBinding
    private var barChartParam: BarChartParam? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBarChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureChartAppearance()
        prepareChartData(createChartData())
    }

    fun setChartBarData(param: BarChartParam) {
        barChartParam = param
    }

    private fun configureChartAppearance() {

        binding.chart.apply {
            setFitBars(true)
            setPinchZoom(false)
            setScaleEnabled(false)
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            setDrawGridBackground(false)

            description.isEnabled = false
            isClickable = false

            xAxis.apply {
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val position = value.toInt()
                        if (position >= 0 && position < pokemonStateTypes.size) {
                            Log.i(this.javaClass.name, "Value: ${pokemonStateTypes[value.toInt()]}")
                            return pokemonStateTypes[position]
                        }
                        return ""
                    }
                }

                textSize = 10f
                labelRotationAngle = 45f
                position = XAxis.XAxisPosition.BOTTOM
                labelCount = pokemonStateTypes.size
                animateXY(1000, 1000)
            }
        }
    }

    private fun createChartData(): BarData {

        val barDataSetTemp = mutableListOf<BarDataSet>()
        val barMapEntry = mutableMapOf<String, MutableList<BarEntry>>()

        barChartParam?.data?.let { barData ->

            for (index in barData.indices) {

                barMapEntry["$index"] = mutableListOf()

                for (labelIndex in barData[index].label.indices) {
                    barMapEntry["$index"]?.add(
                        BarEntry(
                            labelIndex.toFloat(),
                            barData[index].value[labelIndex].toFloat()
                        )
                    )
                }

                val dataset = BarDataSet(
                    barMapEntry["$index"]!!.toList(),
                    barData[index].name
                ).apply {
                    color = availableColor[index]
                }
                barDataSetTemp.add(dataset)
            }
        }

        return BarData(barDataSetTemp.toList())
    }

    private fun prepareChartData(dataset: BarData) {
        val color = getSystemTextColor()
        binding.chart.apply {

            data = dataset

            if ((barChartParam?.data?.size ?: 0) > 1) {
                data.barWidth = 0.3f
                data.isHighlightEnabled = false
                xAxis.axisMinimum  = -0.5f
                xAxis.mAxisMaximum = dataset.entryCount - 0.5f + 0.3f * (dataset.entryCount - 1)
                groupBars(0f, 0.3f, 0f)
            }

            xAxis.textColor = color
            setChartBarLegend()
            setBorderColor(color)
            setGridBackgroundColor(color)
            data.setValueTextColor(color)

            xAxis.setCenterAxisLabels(true)

            invalidate()
        }
    }

    private fun getSystemTextColor(): Int {
        val isDarkMode = requireContext().resources.isDarkMode()
        val color = if (isDarkMode) R.color.white else R.color.black
        return ContextCompat.getColor(requireContext(), color)
    }
    private fun setChartBarLegend() {

        binding.chart.legend.apply {
            isEnabled = true
            textColor = getSystemTextColor()
            textSize = 10f
            yEntrySpace = 0f
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            orientation = Legend.LegendOrientation.HORIZONTAL
            setDrawInside(true)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = BarChartFragment()
    }
}