package com.osho.powerweight.UI

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.osho.powerweight.Models.Student
import com.osho.powerweight.Providers.AuthenticationProvider
import com.osho.powerweight.Providers.DataProviderNetwork
import com.osho.powerweight.Utils.Constants
import com.osho.powerweight.Utils.GlideLoader
import com.osho.powerweight.Utils.TinyDB
import com.osho.powerweight.databinding.ActivityDashBoardBinding
import java.io.IOException


class DashBoardActivity : BaseActivity() {

    private lateinit var binding : ActivityDashBoardBinding
    private var mAuth : AuthenticationProvider = AuthenticationProvider()
    var student = Student()
    private var mSelectedImageFileUri: Uri? = null
    private var mImageURL : String = ""

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tinyDB  = TinyDB(this)
        student = tinyDB.getObject(Constants.KEY, Student::class.java)

        binding.cvProfile.setOnClickListener {
            startActivity(Intent(this,ProfileActivity::class.java))
        }
        binding.cvAssistance.setOnClickListener {
            startActivity(Intent(this,AssistanceActivity::class.java))
        }
        binding.cvHealth.setOnClickListener {
            startActivity(Intent(this,HealthActivity::class.java))
        }
        binding.cvGraphics.setOnClickListener {
            startActivity(Intent(this,GraphicsActivity::class.java))
        }
        binding.cvLogout.setOnClickListener {
            mAuth.logout(this)
        }

        binding.updatePhoto.setOnClickListener {
            setupPermission()
        }
        binding.refresh.setOnClickListener {
            onResume()
            Toast.makeText(this,"Datos Actualizados!",Toast.LENGTH_SHORT).show()
        }
        getDateTime()
        doBackgroundWeight()
    }

    private fun setupPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            Constants.showImageChooser(this)
        } else {
            /*Requests permissions to be granted to this application. These permissions
             must be requested in your manifest, they should not be granted to your app,
             and they should have protection level*/
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.READ_STORAGE_PERMISSION_CODE
            )
        }
    }

    /**
     * This function will identify the result of runtime permission after the user allows or deny permission based on the unique code.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                   "Permiso de lectura denegado!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {

            mSelectedImageFileUri = data.data!!

            try {
                uploadProductImage()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadProductImage() {
        showProgressDialog()
        DataProviderNetwork().uploadImageToCloudStorage(this,mSelectedImageFileUri,Constants.IMAGE_PROFILE)
    }

    fun imageUploadSuccess(imageURL: String) {
        val db  = TinyDB(this)
        val student =  db.getObject(Constants.KEY,Student::class.java)
        // Initialize the global image url variable.
        mImageURL = imageURL
        DataProviderNetwork().uploadImageDetails(imageURL,student.rfid).addOnCompleteListener { task ->
            if(task.isSuccessful){
                student.image_profile = imageURL
                db.putObject(Constants.KEY,student)
                Toast.makeText(this, "Subido!", Toast.LENGTH_LONG).show()
                binding.profileImage.setImageResource(0)
                GlideLoader(this).loadPicture(mSelectedImageFileUri!!, binding.profileImage)
                hideProgressDialog()
            }
        }.addOnCanceledListener {
            Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show()
            hideProgressDialog()
        }.addOnFailureListener {
            Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show()
            hideProgressDialog()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getDateTime(){
        val tinyDB  = TinyDB(this)
        var student = Student()
        val preference : SharedPreferences = getSharedPreferences(Constants.SUCCESS,MODE_PRIVATE)
        val data = preference.getString(Constants.KEY_SUCCESS,"").toString()

        val preference2 : SharedPreferences = getSharedPreferences(Constants.ACTUAL_DATE_TIME,MODE_PRIVATE)
        val ultimateDate = preference2.getString(Constants.KEY_ACTUAL_DATE_TIME,"").toString()
        binding.actualDate.text = ultimateDate

        if (data != ""){
            student = tinyDB.getObject(Constants.KEY,Student::class.java)
            binding.txtFullname.text = student.fullname
            binding.txtCode.text = "Código de estudiante : "+student.code_student
        }
    }

    override fun onResume() {
        getDateTime()
        doBackgroundWeight()
        super.onResume()
    }

    private fun doBackgroundWeight(){
        val tinyDB  = TinyDB(this)
        val student =  tinyDB.getObject(Constants.KEY,Student::class.java)

        GlideLoader(this).loadPicture(student.image_profile,binding.profileImage)
        DataProviderNetwork().getAllData(this,student.rfid)
        DataProviderNetwork().getWeight(this,student.code_student)
        DataProviderNetwork().getRegisters(this,student.rfid)
        DataProviderNetwork().verifyMonths(this,student.timestamp,student.rfid,binding)
    }


    override fun onBackPressed() {
    }

}