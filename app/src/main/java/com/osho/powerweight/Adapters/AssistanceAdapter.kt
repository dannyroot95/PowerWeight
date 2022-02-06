package com.osho.powerweight.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.osho.powerweight.Models.Registers
import com.osho.powerweight.R
import kotlinx.android.synthetic.main.items_registers.view.*
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

open class AssistanceAdapter(private val context: Context,
                             private var list: ArrayList<Registers>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.items_registers,
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        var myPosition = 0
        val sdfDate = SimpleDateFormat("dd/MM/yyyy")
        val netDate = Date(model.timestamp)
        val myDate = sdfDate.format(netDate)
        val myWeight = model.weight.split("kg").toTypedArray()
        var myHeight = model.height
        if (myHeight == ""){
            myHeight = "1.60"
        }
        val imc = myWeight[0].toDouble()/(myHeight.toDouble()*myHeight.toDouble())
        val rounded = imc.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
        val stIMC = rounded.toString()

        if (holder is MyViewHolder) {
            myPosition += 1
            holder.itemView.it_position.text = "Asistencia #$myPosition"
            holder.itemView.it_date.text = "Fecha : $myDate"
            holder.itemView.it_weight.text = model.weight
            holder.itemView.it_imc.text = "IMC : $stIMC"

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}