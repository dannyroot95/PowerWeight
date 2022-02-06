package com.osho.powerweight.UI

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.osho.powerweight.Models.Student
import com.osho.powerweight.Providers.DataProviderNetwork
import com.osho.powerweight.R
import com.osho.powerweight.Utils.Constants
import com.osho.powerweight.Utils.IMC
import com.osho.powerweight.Utils.TinyDB
import com.osho.powerweight.databinding.ActivityHealthBinding
import com.osho.powerweight.databinding.DialogCausesBinding
import com.osho.powerweight.databinding.DialogDietBinding
import java.math.RoundingMode

open class HealthActivity : AppCompatActivity() {

    private lateinit var binding : ActivityHealthBinding
    private lateinit var bindingDiet : DialogDietBinding
    private lateinit var dialogDiet: Dialog
    private lateinit var dialogConsequences: Dialog
    private lateinit var bindingCauses : DialogCausesBinding
    var student = Student()
    private lateinit var dataIMC : IMC

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHealthBinding.inflate(layoutInflater)
        bindingDiet = DialogDietBinding.inflate(layoutInflater)
        bindingCauses = DialogCausesBinding.inflate(layoutInflater)

        val db = TinyDB(this)
        if (db.getListDouble(Constants.WEIGHT_LIST).isEmpty()){
            Toast.makeText(this,"Sin registros!", Toast.LENGTH_SHORT).show()
            finish()
        }

        dialogDiet = Dialog(this)
        dialogDiet.window?.setBackgroundDrawable(ColorDrawable(0))
        dialogDiet.setContentView(bindingDiet.root)
        dialogConsequences = Dialog(this)
        dialogConsequences.window?.setBackgroundDrawable(ColorDrawable(0))
        dialogConsequences.setContentView(bindingCauses.root)

        setContentView(binding.root)
        setLineChartData()
        dataIMC = IMC()

        var weight = ""
        val preference: SharedPreferences = getSharedPreferences(Constants.GET_LAST_WEIGHT, MODE_PRIVATE)
        weight = preference.getString(Constants.KEY_WEIGHT, "").toString()

        if (weight != "") {
            val tinyDB = TinyDB(this)
            student = tinyDB.getObject(Constants.KEY, Student::class.java)
            val imc = weight.toDouble() / (student.height.toDouble() * student.height.toDouble())
            val rounded = imc.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
            val stIMC = rounded.toString()
            binding.height.text = student.height + "Mtrs"
            binding.weight.text = weight + "kg"
            binding.imc.text = stIMC
            binding.compositionBody.text = dataIMC.valueIMC(rounded)
            val myHeight = student.height.split(".")
            val idealWeight =
                ((myHeight[0] + myHeight[1]).toInt() - 100 + ((student.age.toInt() / 10) * 0.9)).toBigDecimal()
                    .setScale(0, RoundingMode.UP).toInt()
            binding.idealWeight.text = idealWeight.toString() + "kg"
            getValueImageIMC(dataIMC.valueIMC(rounded))
            getData()
        } else {
            getData()
        }

        binding.btnDiet.setOnClickListener {
            val textStatusHealth = binding.compositionBody.text.toString()
            dialogDiet.show()
            if (textStatusHealth.contains("Sobre") || textStatusHealth.contains("Obesidad")) {
                bindingDiet.dietText.text = Constants.DIET_OBESIDAD
            } else if (textStatusHealth.contains("Delgadez")) {
                bindingDiet.dietText.text = Constants.DIET_DELGADEZ
            } else if (textStatusHealth.contains("saludable")) {
                bindingDiet.dietText.text = Constants.DIET_NORMAL
                bindingDiet.dialogBtnConsequences.visibility = View.GONE
            }
            bindingDiet.closeDialog.setOnClickListener {
                dialogDiet.dismiss()
            }

            bindingDiet.dialogBtnConsequences.setOnClickListener {
                dialogConsequences.show()
                if (textStatusHealth.contains("Sobre") || textStatusHealth.contains("Obesidad")) {
                    bindingCauses.causesText.text = Constants.CAUSE_OBESIDAD
                } else if (textStatusHealth.contains("Delgadez")) {
                    bindingCauses.causesText.text = Constants.CAUSE_DELGADEZ
                }
                bindingCauses.closeDialog.setOnClickListener {
                    dialogConsequences.dismiss()
                }
            }

        }
    }

    @SuppressLint("SetTextI18n")
    private fun getData(){
        DataProviderNetwork().getDataParameters(this,binding)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getValueImageIMC(value : String){
        when (value) {
            Constants.DEL_MUY_SEVERA -> {
                binding.imvImc.setImageDrawable(resources.getDrawable(R.drawable.del_muy_severa))
            }
            Constants.DEL_SEVERA -> {
                binding.imvImc.setImageDrawable(resources.getDrawable(R.drawable.del_severa))
            }
            Constants.DELGADEZ -> {
                binding.imvImc.setImageDrawable(resources.getDrawable(R.drawable.delgadez))
            }
            Constants.PESO_SALUDABLE -> {
                binding.imvImc.setImageDrawable(resources.getDrawable(R.drawable.peso_normal))
            }
            Constants.SOBRE_PESO -> {
                binding.imvImc.setImageDrawable(resources.getDrawable(R.drawable.sobre_peso))
            }
            Constants.OB_MODERADA -> {
                binding.imvImc.setImageDrawable(resources.getDrawable(R.drawable.ob_moderada))
            }
            Constants.OB_SEVERA -> {
                binding.imvImc.setImageDrawable(resources.getDrawable(R.drawable.ob_severa))
            }
            else -> {
                binding.imvImc.setImageDrawable(resources.getDrawable(R.drawable.ob_muy_severa))
            }
        }
    }

    private fun setLineChartData() {
        val tinyDB  = TinyDB(this)
        val student =  tinyDB.getObject(Constants.KEY,Student::class.java)
        val height = student.height.toDouble()
        var array : ArrayList<Double> = ArrayList()
        array = tinyDB.getListDouble(Constants.WEIGHT_LIST)
        val linevalues = ArrayList<Entry>()

        for (i in 0 until array.size){
            val imc = (array[i]/(height*height)).toFloat()
            linevalues.add(Entry(array[i].toFloat(), imc))
        }

        val linedataset = LineDataSet(linevalues, "Indice de masa corporal")
        linedataset.color = resources.getColor(R.color.purple_500)

        linedataset.setDrawFilled(true)
        linedataset.valueTextSize = 14F
        linedataset.setCircleColor(Color.WHITE)
        linedataset.circleHoleColor = Color.GRAY
        linedataset.fillColor = resources.getColor(R.color.purple_500)
        linedataset.mode = LineDataSet.Mode.CUBIC_BEZIER

        //We connect our data to the UI Screen
        val data = LineData(linedataset)
        binding.getTheGraph.data = data
        binding.getTheGraph.setBackgroundColor(resources.getColor(R.color.white))
        binding.getTheGraph.animateXY(2000, 2000, Easing.EaseInCubic)

    }

}