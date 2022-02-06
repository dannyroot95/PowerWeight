package com.osho.powerweight.UI

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.osho.powerweight.Models.Student
import com.osho.powerweight.R
import com.osho.powerweight.Utils.Constants
import com.osho.powerweight.Utils.IMC
import com.osho.powerweight.Utils.TinyDB
import com.osho.powerweight.databinding.ActivityAssistanceBinding
import com.osho.powerweight.databinding.ActivityGraphicsBinding
import kotlinx.android.synthetic.main.activity_health.*
import java.math.RoundingMode

class GraphicsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityGraphicsBinding
    private lateinit var pieChart: PieChart


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGraphicsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pieChart = binding.pieChart

        val db = TinyDB(this)
        if (db.getListDouble(Constants.WEIGHT_LIST).isEmpty()){
            Toast.makeText(this,"Sin registros!", Toast.LENGTH_SHORT).show()
            finish()
        }else{
            setLineChartData()
            setLineChartDataIMC()
        }

    }

    private fun setLineChartData() {
        val tinyDB  = TinyDB(this)
        var array : ArrayList<Double> = ArrayList()
        array = tinyDB.getListDouble(Constants.WEIGHT_LIST_NORMAL)
        val linevalues = ArrayList<Entry>()
        var index = 0

        for (i in 0 until array.size){
            index += 1
            linevalues.add(Entry(index.toFloat(),array[i].toFloat()))
        }

        val linedataset = LineDataSet(linevalues, "Peso")
        //We add features to our chart
        linedataset.color = resources.getColor(R.color.teal_200)

        linedataset.setDrawFilled(true)
        linedataset.valueTextSize = 14F
        linedataset.setCircleColor(Color.WHITE)
        linedataset.circleHoleColor = Color.GRAY
        linedataset.fillColor = resources.getColor(R.color.teal_200)
        linedataset.mode = LineDataSet.Mode.CUBIC_BEZIER

        //We connect our data to the UI Screen
        val data = LineData(linedataset)
        binding.graphicWeight.data = data
        binding.graphicWeight.setBackgroundColor(resources.getColor(R.color.white))
        binding.graphicWeight.animateXY(2000, 2000, Easing.EaseInCubic)

    }

    private fun setLineChartDataIMC() {
        val tinyDB  = TinyDB(this)
        val student =  tinyDB.getObject(Constants.KEY,Student::class.java)
        val height = student.height.toDouble()
        var array : ArrayList<Double> = ArrayList()
        array = tinyDB.getListDouble(Constants.WEIGHT_LIST_NORMAL)
        val linevalues = ArrayList<Entry>()
        var index = 0
        var imcPlus = 0.0
        var weight = 0.0

        for (i in 0 until array.size){
            index += 1
            weight += array[i]
            val imc = (array[i]/(height*height)).toFloat()
            imcPlus += imc
            linevalues.add(Entry(index.toFloat(),imc))
        }

        var imcAverage = imcPlus/array.size
        val weightAverage = weight/array.size

        if (imcAverage != 0.0){
            imcAverage = imcPlus/array.size
            val rounded = imcAverage.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
            val roundedWeight = weightAverage.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
            getValueImageIMC(IMC().valueIMC(rounded))
            binding.imcAverage.text = rounded.toString()

            val myHeight = student.height.split(".")
            val idealWeight = ((myHeight[0]+myHeight[1]).toInt() - 100 + ((student.age.toInt()/10)*0.9)).toBigDecimal().setScale(0, RoundingMode.UP).toFloat()



            initPieChart(rounded.toFloat(),roundedWeight.toFloat(),idealWeight)


        }else{
            binding.imcAverage.text = "Sin datos"
        }


        val linedataset = LineDataSet(linevalues, "Índice de masa corporal")
        //We add features to our chart
        linedataset.color = resources.getColor(R.color.orange)

        linedataset.setDrawFilled(true)
        linedataset.valueTextSize = 14F
        linedataset.setCircleColor(Color.WHITE)
        linedataset.circleHoleColor = Color.GRAY
        linedataset.fillColor = resources.getColor(R.color.orange)
        linedataset.mode = LineDataSet.Mode.CUBIC_BEZIER

        //We connect our data to the UI Screen
        val data = LineData(linedataset)
        binding.graphicIMC.data = data
        binding.graphicIMC.setBackgroundColor(resources.getColor(R.color.white))
        binding.graphicIMC.animateXY(2000, 2000, Easing.EaseInCubic)

    }

    private fun getValueImageIMC(value : String){
        when (value) {
            Constants.DEL_MUY_SEVERA -> {
                binding.imcStatus.text = "Delgadez muy severa"
            }
            Constants.DEL_SEVERA -> {
                binding.imcStatus.text = "Delgadez severa"
            }
            Constants.DELGADEZ -> {
                binding.imcStatus.text = "Delgadez"
            }
            Constants.PESO_SALUDABLE -> {
                binding.imcStatus.text = "Peso saludable"
            }
            Constants.SOBRE_PESO -> {
                binding.imcStatus.text = "Sobre peso"
            }
            Constants.OB_MODERADA -> {
                binding.imcStatus.text = "Obesidad moderada"
            }
            Constants.OB_SEVERA -> {
                binding.imcStatus.text = "Obesidad severa"
            }
            else -> {
                binding.imcStatus.text = "Obesidad muy severa"
            }
        }
    }

    private fun initPieChart(imcAverage : Float ,weightAverage : Float ,idealWeight : Float) {
        //pieChart.setUsePercentValues(true)
        pieChart.description.text = ""
        //hollow pie chart
        pieChart.isDrawHoleEnabled = false
        pieChart.setTouchEnabled(false)
        pieChart.setDrawEntryLabels(false)
        //adding padding
        pieChart.setExtraOffsets(20f, 0f, 20f, 20f)
        //pieChart.setUsePercentValues(true)
        pieChart.isRotationEnabled = false
        pieChart.setDrawEntryLabels(false)
        pieChart.legend.orientation = Legend.LegendOrientation.VERTICAL
        pieChart.legend.isWordWrapEnabled = true

        setDataToPieChart(imcAverage, weightAverage, idealWeight)

    }

    private fun setDataToPieChart(imcAverage : Float ,weightAverage : Float ,idealWeight : Float) {
        //pieChart.setUsePercentValues(true)

        val dataEntries = ArrayList<PieEntry>()
        dataEntries.add(PieEntry(weightAverage, "Peso Promedio"))
        dataEntries.add(PieEntry(idealWeight, "Peso Ideal"))
        dataEntries.add(PieEntry(imcAverage, "IMC Promedio"))

        val colors: ArrayList<Int> = ArrayList()
        colors.add(Color.parseColor("#1BB2FE"))
        colors.add(Color.parseColor("#FF4242"))
        colors.add(Color.parseColor("#FFCB42"))

        val dataSet = PieDataSet(dataEntries, "")
        val data = PieData(dataSet)
        // In Percentage
        //data.setValueFormatter(PercentFormatter())
        dataSet.sliceSpace = 3f
        dataSet.colors = colors
        pieChart.data = data
        data.setValueTextSize(15f)
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        pieChart.animateY(1400, Easing.EaseInOutQuad)

        //create hole in center
        pieChart.holeRadius = 58f
        pieChart.transparentCircleRadius = 61f
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)


        //add text in center
        pieChart.setDrawCenterText(true);
        pieChart.centerText = "Analisis de parámetros"


        pieChart.invalidate()

    }

}