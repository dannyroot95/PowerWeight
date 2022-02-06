package com.osho.powerweight.UI

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.osho.powerweight.Models.Student
import com.osho.powerweight.Providers.DataProviderNetwork
import com.osho.powerweight.Utils.Constants
import com.osho.powerweight.Utils.GlideLoader
import com.osho.powerweight.Utils.TinyDB
import com.osho.powerweight.databinding.ActivityProfileBinding
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : BaseActivity() {

    private lateinit var binding : ActivityProfileBinding
    private var mSelectedImageFileUri: Uri? = null
    private var mImageURL : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val student = TinyDB(this).getObject(Constants.KEY, Student::class.java)

        binding.ivUpdate.setOnClickListener {
            setupPermission()
        }

        setupUI(student)
        DataProviderNetwork().getStatus(student.rfid,binding)
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
                GlideLoader(this).loadPicture(mSelectedImageFileUri!!, binding.ivImage)
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

    @SuppressLint("SimpleDateFormat")
    private fun setupUI(student : Student){

        GlideLoader(this).loadPicture(student.image_profile,binding.ivImage)

        if(student.status == "Pagado"){
            binding.btnPay.visibility = View.GONE
            binding.status.text = student.status
            binding.status.setTextColor(Color.parseColor("#03A112"))
        }else{
            binding.btnPay.visibility = View.VISIBLE
            binding.status.text = student.status
            binding.status.setTextColor(Color.parseColor("#FC0000"))
        }
        binding.btnPay.setOnClickListener {
            startActivity(Intent(this,CheckoutActivity::class.java))
        }

        binding.edtFullname.setText(student.fullname)

        if (student.sex == Constants.MALE){
            binding.imgMale.visibility = View.VISIBLE
        }
        else{
            binding.imgFemale.visibility = View.VISIBLE
        }

        binding.edtCode.setText(student.code_student)
        binding.edtDni.setText(student.dni_student)
        binding.edtAge.setText(student.age)
        binding.edtCarrer.setText(student.carrer)
        binding.edtCodeRfid.setText(student.rfid)

        val sdfDate = SimpleDateFormat("dd/MM/yyyy")
        val netDate = Date(student.timestamp)
        val myDate = sdfDate.format(netDate)
        binding.edtDatePay.setText(myDate)

    }

}