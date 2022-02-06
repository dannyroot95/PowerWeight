package com.osho.powerweight

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.osho.powerweight.Models.Student
import com.osho.powerweight.Providers.AuthenticationProvider
import com.osho.powerweight.UI.DashBoardActivity
import com.osho.powerweight.UI.UpdateProfileData
import com.osho.powerweight.Utils.Constants
import com.osho.powerweight.Utils.TinyDB
import com.osho.powerweight.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var bindind : ActivityMainBinding
    private lateinit var mAuth : AuthenticationProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindind.root)

        mAuth = AuthenticationProvider()

        val preference : SharedPreferences = getSharedPreferences(Constants.CREDENTIALS,MODE_PRIVATE)
        val credential = preference.getString(Constants.KEY_CREDENTIALS,"")
        if (credential != ""){
            val getData = credential!!.split("-").toTypedArray()
            bindind.switchCredential.isChecked = true
            bindind.edtCode.setText(getData[0])
            bindind.edtDni.setText(getData[1])
            bindind.edtCode.isEnabled = false
            bindind.edtDni.isEnabled = false
        }


        bindind.btnLogin.setOnClickListener {
            login()
        }

        bindind.switchCredential.setOnClickListener {
            val editor = preference.edit()
            if(bindind.edtCode.text.toString() != "" && bindind.edtDni.text.toString() != ""){
                if (bindind.edtCode.length() == 8 && bindind.edtDni.length() == 8){
                    if (bindind.switchCredential.isChecked){
                        editor.putString(Constants.KEY_CREDENTIALS,bindind.edtCode.text.toString()+"-"+bindind.edtDni.text.toString())
                        editor.apply()
                        bindind.edtCode.isEnabled = false
                        bindind.edtDni.isEnabled = false
                        Toast.makeText(this, "Guardado!", Toast.LENGTH_SHORT).show()
                    }else{
                        bindind.edtCode.isEnabled = true
                        bindind.edtDni.isEnabled = true
                        getSharedPreferences(Constants.CREDENTIALS, Context.MODE_PRIVATE).edit().clear().apply()
                        Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    bindind.switchCredential.isChecked = false
                    Toast.makeText(this@MainActivity,"Verifique su código de estudiante y DNI!",Toast.LENGTH_SHORT).show()
                }
            }else{
                bindind.switchCredential.isChecked = false
                Toast.makeText(this@MainActivity,"Complete los campos",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun login() {
        val code = bindind.edtCode.text.toString()
        val dni = bindind.edtDni.text.toString()
        val tinyDB  = TinyDB(this)
        val sharedPreferences = getSharedPreferences(Constants.SUCCESS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val sharedPreferencesProfile = getSharedPreferences(Constants.PROFILE_COMPLETED, Context.MODE_PRIVATE)
        val editorProfile = sharedPreferencesProfile.edit()

        if(code.isNotEmpty() && dni.isNotEmpty()){
            if (code.length == 8) {
                if (dni.length == 8) {
                    bindind.progress.visibility = View.VISIBLE
                    bindind.btnLogin.isEnabled = false
                    bindind.edtCode.isEnabled = false
                    bindind.edtDni.isEnabled = false
                    mAuth.login().orderByChild(Constants.DNI).equalTo(dni).addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()){
                                for (postSnapshot in snapshot.children){
                                        val code_s = postSnapshot.child("code_student").value.toString()
                                        val dni_s  = postSnapshot.child("dni_student").value.toString()
                                        val url  = postSnapshot.child("image_profile").value.toString()

                                        val profileCompleted = postSnapshot.child("profile_completed").value.toString()
                                        if (code_s == code && dni_s == dni){
                                            val student = postSnapshot.getValue<Student>()
                                            tinyDB.putObject(Constants.KEY,student)
                                            editor.putString(Constants.KEY_SUCCESS,Constants.SIGN_IN)
                                            editor.apply()
                                            editorProfile.putString(Constants.KEY_PROFILE,profileCompleted)
                                            editorProfile.apply()

                                            if (profileCompleted != "0"){
                                                startActivity(Intent(this@MainActivity,DashBoardActivity::class.java))
                                            }else{
                                                startActivity(Intent(this@MainActivity,UpdateProfileData::class.java))
                                            }
                                        }
                                        else{
                                            bindind.progress.visibility = View.GONE
                                            bindind.btnLogin.isEnabled = true
                                            bindind.edtCode.isEnabled = true
                                            bindind.edtDni.isEnabled = true
                                            Toast.makeText(this@MainActivity,"Verifique sus datos!",Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }else{
                                bindind.progress.visibility = View.GONE
                                bindind.btnLogin.isEnabled = true
                                bindind.edtCode.isEnabled = true
                                bindind.edtDni.isEnabled = true
                                Toast.makeText(this@MainActivity,"No existe el usuario!",Toast.LENGTH_SHORT).show()

                            }

                        }
                        override fun onCancelled(error: DatabaseError) {
                            bindind.progress.visibility = View.GONE
                            bindind.btnLogin.isEnabled = true
                            bindind.edtCode.isEnabled = true
                            bindind.edtDni.isEnabled = true
                            Toast.makeText(this@MainActivity,"error",Toast.LENGTH_SHORT).show()
                        }
                    })
                }else{
                    Toast.makeText(this@MainActivity,"DNI inválido!",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this@MainActivity,"Código de estudiante inválido!",Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this@MainActivity,"Complete los campos!",Toast.LENGTH_SHORT).show()
        }

    }

    override fun onStart() {
        val preference : SharedPreferences = getSharedPreferences(Constants.SUCCESS,MODE_PRIVATE)
        val preferenceProfile : SharedPreferences = getSharedPreferences(Constants.PROFILE_COMPLETED,MODE_PRIVATE)
        val data = preference.getString(Constants.KEY_SUCCESS,"").toString()
        val profile = preferenceProfile.getString(Constants.KEY_PROFILE,"").toString()

        if (data != ""){
            if (profile != "0"){
                startActivity(Intent(this,DashBoardActivity::class.java))
                overridePendingTransition(
                    android.R.anim.fade_in,android.R.anim.fade_out)
                finish()
            }
            else{
                startActivity(Intent(this,UpdateProfileData::class.java))
                overridePendingTransition(
                    android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                finish()
            }
        }

        super.onStart()
    }


    override fun onBackPressed() {
    }

}