package com.osho.powerweight.Providers

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.osho.powerweight.MainActivity
import com.osho.powerweight.Models.Student
import com.osho.powerweight.UI.DashBoardActivity
import com.osho.powerweight.Utils.Constants
import com.osho.powerweight.Utils.TinyDB
import com.osho.powerweight.databinding.ActivityUpdateProfileDataBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File


class AuthenticationProvider {

    var mDatabaseReference : DatabaseReference = Firebase.database.reference

    fun login() : DatabaseReference {
        return mDatabaseReference.child(Constants.USERS).child(Constants.STUDENTS)
    }

    @SuppressLint("CommitPrefEdits")
    fun logout(context: Context) {
        val tinyDB  = TinyDB(context)
        val sharedPreferences = context.getSharedPreferences(Constants.SUCCESS, Context.MODE_PRIVATE)
        val sharedPreferencesWeight = context.getSharedPreferences(Constants.GET_LAST_WEIGHT, Context.MODE_PRIVATE)
        tinyDB.remove(Constants.KEY)
        tinyDB.remove(Constants.REGISTERS)
        tinyDB.remove(Constants.WEIGHT_LIST_NORMAL)
        tinyDB.remove(Constants.WEIGHT_LIST)
        sharedPreferences.edit().remove(Constants.KEY_SUCCESS).apply()
        sharedPreferencesWeight.edit().remove(Constants.KEY_WEIGHT).apply()
        context.startActivity(Intent(context, MainActivity::class.java))
    }

    fun update(rfid : String,context: Context,age : String, height : String,binding : ActivityUpdateProfileDataBinding){
        val sharedPreferencesProfile = context.getSharedPreferences(Constants.PROFILE_COMPLETED, Context.MODE_PRIVATE)
        val editorProfile = sharedPreferencesProfile.edit()
        val map: MutableMap<String, Any> = HashMap()
        map["age"] = age
        map["height"] = height
        map["profile_completed"] = "1"
      mDatabaseReference.child(Constants.USERS).child(Constants.STUDENTS).child(rfid).updateChildren(map).addOnCompleteListener {
          if (it.isSuccessful){
              mDatabaseReference.child(Constants.REGISTERS).orderByChild(Constants.RFID).equalTo(rfid).addListenerForSingleValueEvent(object : ValueEventListener{
                  override fun onDataChange(snapshot: DataSnapshot) {
                      if (snapshot.exists()){
                          val mapx: MutableMap<String, Any> = HashMap()
                          mapx["age"] = age
                          mapx["height"] = height
                          for (i in snapshot.children){
                              mDatabaseReference.child(Constants.REGISTERS).child(i.key!!).updateChildren(mapx)
                          }
                      }else{
                          //Toast.makeText(context,"Error de servidor",Toast.LENGTH_SHORT).show()
                      }
                  }
                  override fun onCancelled(error: DatabaseError) {
                      Toast.makeText(context,"Error al actualizar registros",Toast.LENGTH_SHORT).show()
                  }
              })
              val tinyDB  = TinyDB(context)
              var student = Student()
              student = tinyDB.getObject(Constants.KEY,Student::class.java)
              student.age = age
              student.height = height
              tinyDB.putObject(Constants.KEY,student)
              editorProfile.putString(Constants.KEY_PROFILE,"1")
              editorProfile.apply()
              binding.pbUpdate.visibility = View.GONE
              context.startActivity(Intent(context, DashBoardActivity::class.java))
          }

      }
    }

}