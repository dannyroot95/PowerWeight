package com.osho.powerweight.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.osho.powerweight.Adapters.AssistanceAdapter
import com.osho.powerweight.Models.Registers
import com.osho.powerweight.Models.Student
import com.osho.powerweight.Providers.DataProviderNetwork
import com.osho.powerweight.Utils.Constants
import com.osho.powerweight.Utils.TinyDB
import com.osho.powerweight.databinding.ActivityAssistanceBinding

class AssistanceActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAssistanceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssistanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val db = TinyDB(this)
        val dataStudent = db.getObject(Constants.KEY, Student::class.java)

        if (db.getListObjectRegister(Constants.REGISTERS,Registers::class.java).isEmpty()){
            Toast.makeText(this,"Sin registros!",Toast.LENGTH_SHORT).show()
            finish()
        }

        DataProviderNetwork().getRegisters(this,dataStudent.rfid)
    }

    fun successDataFromServer(list: ArrayList<Registers>){
        if (list.size > 0){
            binding.progressCircular.visibility = View.GONE
            binding.lnTitle.visibility = View.VISIBLE
            binding.recyclerRegisters.visibility = View.VISIBLE
            binding.recyclerRegisters.layoutManager = LinearLayoutManager(this)
            binding.recyclerRegisters.setHasFixedSize(true)
            val adapter = AssistanceAdapter(this, list)
            binding.recyclerRegisters.adapter = adapter
        }else{
            binding.progressCircular.visibility = View.GONE
            Toast.makeText(this,"No hay datos!",Toast.LENGTH_LONG).show()
        }
    }
}