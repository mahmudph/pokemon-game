package id.myone.pokemongame.ui.barchart

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import id.myone.pokemongame.common.pokemonStateTypes
import id.myone.pokemongame.databinding.FragmentBarChartBinding

class BarChartFragment : Fragment() {

    private lateinit var binding: FragmentBarChartBinding

//    private var labels = emptyList<String>()
//    private var data = emptyList<Int>()

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

            animateXY(1000, 1000)

            legend.isEnabled = false
            isClickable = false

            xAxis.apply {
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        Log.i(this.javaClass.name, "Value: ${pokemonStateTypes[value.toInt()]}")
                        return pokemonStateTypes[value.toInt()]
                    }
                }

                textSize = 12f
                labelCount = pokemonStateTypes.size
                labelRotationAngle = 45f
                xAxis.granularity = 1f
                position = XAxis.XAxisPosition.BOTTOM
                setDrawLabels(true)
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

                barDataSetTemp.add(BarDataSet(barMapEntry["$index"]!!.toList(), barData[index].name))
            }
        }

        return BarData(barDataSetTemp.toList())
    }

    private fun prepareChartData(dataset: BarData) {
        dataset.setValueTextSize(12f)
        binding.chart.data = dataset
        binding.chart.invalidate()
    }

    companion object {
        @JvmStatic
        fun newInstance() = BarChartFragment()
    }
}