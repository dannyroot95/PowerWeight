package com.osho.powerweight.UI

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.osho.powerweight.Models.Student
import com.osho.powerweight.Providers.AuthenticationProvider
import com.osho.powerweight.R
import com.osho.powerweight.Utils.Constants
import com.osho.powerweight.Utils.TinyDB
import com.osho.powerweight.databinding.ActivityDashBoardBinding
import com.osho.powerweight.databinding.ActivityUpdateProfileDataBinding

class UpdateProfileData : AppCompatActivity() {

    private lateinit var binding : ActivityUpdateProfileDataBinding
    private lateinit var mAuth : AuthenticationProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = AuthenticationProvider()

        binding.btnUpdate.setOnClickListener {
            update()
        }

    }

    private fun update() {
        val tinyDB  = TinyDB(this)
        var student = Student()
        student = tinyDB.getObject(Constants.KEY,Student::class.java)
        val age = binding.edtEdad.text.toString()
        val height = binding.edtAltura.text.toString()
        val rfid = student.rfid

        if (age.isNotEmpty() && height.isNotEmpty()){
            binding.pbUpdate.visibility = View.VISIBLE
            mAuth.update(rfid,this,age,height,binding)

        }else{
            Toast.makeText(this,"Complete los campos!",Toast.LENGTH_SHORT).show()
        }

    }
}