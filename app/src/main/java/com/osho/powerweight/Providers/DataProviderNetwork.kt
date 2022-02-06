package com.osho.powerweight.Providers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.osho.powerweight.Models.Registers
import com.osho.powerweight.Models.Student
import com.osho.powerweight.UI.AssistanceActivity
import com.osho.powerweight.UI.DashBoardActivity
import com.osho.powerweight.UI.HealthActivity
import com.osho.powerweight.UI.ProfileActivity
import com.osho.powerweight.Utils.Constants
import com.osho.powerweight.Utils.IMC
import com.osho.powerweight.Utils.TinyDB
import com.osho.powerweight.databinding.ActivityDashBoardBinding
import com.osho.powerweight.databinding.ActivityHealthBinding
import com.osho.powerweight.databinding.ActivityProfileBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.math.RoundingMode
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DataProviderNetwork {

    private var mDatabaseReference : DatabaseReference = Firebase.database.reference

    fun getWeight(context: Context,id : String) {
        val tinyDB  = TinyDB(context)
        return mDatabaseReference.child(Constants.REGISTERS).orderByChild(Constants.CODE_STUDENT).equalTo(id).addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val array : ArrayList<Double> = ArrayList()
                    for (data in snapshot.children){
                        val weight = data.child("weight").value.toString().split("kg").toTypedArray()
                        array.add(weight[0].toDouble())
                    }
                    tinyDB.putListDouble(Constants.WEIGHT_LIST_NORMAL,array)
                    tinyDB.putListDouble(Constants.WEIGHT_LIST,OrderByMin(array,array.size))
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun OrderByMin(listNum: ArrayList<Double>, cant: Int) : ArrayList<Double> {
        var tmp = 0.0
        for(x in 0 until cant) {
            for(y in 0 until cant) {
                if(listNum[x] < listNum[y]) {
                    tmp = listNum[y]
                    listNum[y] = listNum[x]
                    listNum[x] = tmp
                }
            }
        }
        return listNum
    }

    fun getDataParameters(activity: HealthActivity,binding : ActivityHealthBinding){

        val tinyDB  = TinyDB(activity)
        val sharedPreferencesWeight = activity.getSharedPreferences(Constants.GET_LAST_WEIGHT, Context.MODE_PRIVATE)
        val editorWeight = sharedPreferencesWeight.edit()
        val student = tinyDB.getObject(Constants.KEY, Student::class.java)
        val dataIMC = IMC()

        mDatabaseReference.child(Constants.REGISTERS).orderByChild(Constants.CODE_STUDENT).equalTo(student.code_student).limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener{
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                for (regSnapshot in snapshot.children){
                    if (regSnapshot.exists()){
                        val data = regSnapshot.child("weight").value.toString().split("kg").toTypedArray()
                        val height = regSnapshot.child("height").value.toString()
                        val stHeight = height.split(".").toTypedArray()
                        val age = regSnapshot.child("age").value.toString()
                        editorWeight.putString(Constants.KEY_WEIGHT,data[0])
                        editorWeight.apply()
                        val imc = data[0].toDouble()/(height.toDouble()*height.toDouble())
                        val rounded = imc.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
                        val stIMC = rounded.toString()
                        binding.height.text = student.height+"Mtrs"
                        binding.imc.text = stIMC
                        binding.weight.text = data[0]+"kg"
                        binding.compositionBody.text = dataIMC.valueIMC(rounded)
                        val idealWeight = ((stHeight[0]+stHeight[1]).toInt() - 100 + ((age.toInt()/10)*0.9)).toBigDecimal().setScale(0, RoundingMode.UP).toInt()
                        binding.idealWeight.text = idealWeight.toString()+"kg"
                        activity.getValueImageIMC(dataIMC.valueIMC(rounded))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity,"ERROR", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun getRegisters(context: Context,id : String){
        mDatabaseReference.child(Constants.REGISTERS).orderByChild(Constants.RFID).equalTo(id).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val list: ArrayList<Registers> = ArrayList()
                    val db = TinyDB(context)
                    for (i in snapshot.children){
                        val rg = i.getValue(Registers::class.java)!!
                        list.add(rg)
                    }
                    db.putListObjectRegister(Constants.REGISTERS,list)
                    when (context) {
                        is AssistanceActivity -> {
                            context.successDataFromServer(list)
                        }
                }
              }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun getStatus(rfid : String,binding: ActivityProfileBinding){
        mDatabaseReference.child(Constants.USERS).child(Constants.STUDENTS).child(rfid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val status = snapshot.child("status").value.toString()
                    val timestamp = snapshot.child("timestamp").value.toString().toLong()
                    val sdfDate = SimpleDateFormat("dd/MM/yyyy")
                    val netDate = Date(timestamp)
                    val myDate = sdfDate.format(netDate)
                    binding.edtDatePay.setText(myDate)

                    if(status == "Pagado"){
                        binding.btnPay.visibility = View.GONE
                        binding.status.text = status
                        binding.status.setTextColor(Color.parseColor("#03A112"))
                    }else{
                        binding.btnPay.visibility = View.VISIBLE
                        binding.status.text = status
                        binding.status.setTextColor(Color.parseColor("#FC0000"))
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun updtateStatus(id : String) : Task<Void> {
        val map: MutableMap<String, Any> = HashMap()
        map["timestamp"] = System.currentTimeMillis()
        map["status"] = "Pagado"
        return mDatabaseReference.child(Constants.USERS).child(Constants.STUDENTS).child(id).updateChildren(map)
    }

    @SuppressLint("CommitPrefEdits")
    fun verifyMonths(context: Activity, currentTime : Long, id: String, binding: ActivityDashBoardBinding){

        val sdf = SimpleDateFormat("dd/MM/yy")
        val sdfx = SimpleDateFormat("dd/MM/yy HH:mm a")
        val netDate = Date(currentTime)
        val netDate2 = Date(System.currentTimeMillis())
        val date1 = sdf.format(netDate)
        val date2 = sdf.format(netDate2)

        val inicio: Calendar = GregorianCalendar()
        val fin: Calendar = GregorianCalendar()
        inicio.time = SimpleDateFormat("dd/MM/yyyy").parse(date1)
        fin.time = SimpleDateFormat("dd/MM/yyyy").parse(date2)
        val difA = fin[Calendar.YEAR] - inicio[Calendar.YEAR]
        val difM = difA * 12 + fin[Calendar.MONTH] - inicio[Calendar.MONTH]

        if (difM >= 3){
            val student = TinyDB(context).getObject(Constants.KEY,Student::class.java)
            student.status = "Deudor"
            TinyDB(context).putObject(Constants.KEY,student)
            val map: MutableMap<String, Any> = HashMap()
            map["status"] = "Deudor"
            mDatabaseReference.child(Constants.USERS).child(Constants.STUDENTS).child(id).updateChildren(map).addOnCompleteListener { task->
                if(task.isSuccessful){
                    val preference : SharedPreferences = context.getSharedPreferences(Constants.ACTUAL_DATE_TIME,MODE_PRIVATE)
                    val editor = preference.edit()
                    editor.putString(Constants.KEY_ACTUAL_DATE_TIME,sdfx.format(System.currentTimeMillis()).toString())
                    editor.apply()
                    Toast.makeText(context,"Usted tiene una deuda pendiente!",Toast.LENGTH_SHORT).show()
                    binding.actualDate.text = sdfx.format(System.currentTimeMillis())
                }
            }
        }else{
            val preference : SharedPreferences = context.getSharedPreferences(Constants.ACTUAL_DATE_TIME,MODE_PRIVATE)
            val editor = preference.edit()
            editor.putString(Constants.KEY_ACTUAL_DATE_TIME,sdfx.format(System.currentTimeMillis()).toString())
            editor.apply()
            binding.actualDate.text = sdfx.format(System.currentTimeMillis())
        }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {
        //getting the storage reference
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(
                activity,
                imageFileURI
            )
        )

        //adding the file to reference
        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                // The image upload is success
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                // Get the downloadable url from the task snapshot
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())
                        // Here call a function of base activity for transferring the result to it.
                        when (activity) {
                            is DashBoardActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                            is ProfileActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                    }
            }
            .addOnFailureListener { exception ->

                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is DashBoardActivity -> {
                        Toast.makeText(activity,"Error al guardar imagen en servidor!",Toast.LENGTH_SHORT).show()
                        activity.hideProgressDialog()
                    }
                    is ProfileActivity -> {
                        Toast.makeText(activity,"Error al guardar imagen en servidor!",Toast.LENGTH_SHORT).show()
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

    fun uploadImageDetails(url : String , id : String) : Task<Void>{
        val map: MutableMap<String, Any> = HashMap()
        map["image_profile"] = url
        return mDatabaseReference.child(Constants.USERS).child(Constants.STUDENTS).child(id).updateChildren(map)
    }


    fun getAllData(context: Context,id:String){
        mDatabaseReference.child(Constants.USERS).child(Constants.STUDENTS).child(id).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val student = snapshot.getValue<Student>()
                    val db = TinyDB(context)
                    db.putObject(Constants.KEY,student)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


}