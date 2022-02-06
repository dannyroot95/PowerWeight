package com.osho.powerweight.UI

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.aukdeclient.Culqi.Functions.Charge
import com.aukdeclient.Culqi.Functions.TokenCulqi
import com.aukdeclient.Culqi.Utils.Validation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.image.mainactivity.Culqui.Callbacks.ChargeCallback
import com.image.mainactivity.Culqui.Callbacks.TokenCallback
import com.osho.powerweight.Models.Card
import com.osho.powerweight.Models.DataCharge
import com.osho.powerweight.Models.Student
import com.osho.powerweight.Providers.DataProviderNetwork
import com.osho.powerweight.R
import com.osho.powerweight.Utils.Constants
import com.osho.powerweight.Utils.TinyDB
import com.osho.powerweight.databinding.ActivityCheckoutBinding
import com.osho.powerweight.databinding.DialogPayBinding
import org.json.JSONObject
import java.text.DecimalFormat
import java.util.*

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCheckoutBinding
    var validation: Validation? = null
    var tokenID : String? = null
    private lateinit var dialog : Dialog
    private lateinit var bindingDialog : DialogPayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindingDialog = DialogPayBinding.inflate(layoutInflater)
        dialog = Dialog(this)
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        dialog.setContentView(bindingDialog.root)
        dialog.setCancelable(false)

        var cardData = Card()
        validation = Validation()
        binding.tilCvv.isEnabled = false
        val months = resources.getStringArray(R.array.Months)
        val years  = resources.getStringArray(R.array.Years)
        var month = ""
        var year  = ""
        val sharedPreferencesCard = getSharedPreferences(
            Constants.USER_CARD_DATA,
            Context.MODE_PRIVATE
        )
        val editorCard = sharedPreferencesCard.edit()
        val gson = Gson()

        val tinyDB  = TinyDB(this)

        val student = tinyDB.getObject(Constants.KEY, Student::class.java)

        binding.switchSaveCard.setOnClickListener{
            if (binding.txtCardnumber.text.toString().isNotEmpty() && binding.txtCvv.text.toString().isNotEmpty()
                && binding.txtEmail.text.toString().isNotEmpty()){
                if (binding.switchSaveCard.isChecked){
                    val sharedCard = Card(
                        binding.txtCardnumber.text.toString(),
                        binding.txtCvv.text.toString(),
                        month,
                        year.toInt(),
                        binding.txtEmail.text.toString()
                    )
                    val json = gson.toJson(sharedCard)
                    editorCard.putString(Constants.KEY_CARD, json)
                    editorCard.apply()
                    Toast.makeText(this, "Guardado", Toast.LENGTH_SHORT).show()
                } else{
                    if (sharedPreferencesCard.contains(Constants.KEY_CARD)){
                        getSharedPreferences(Constants.USER_CARD_DATA, Context.MODE_PRIVATE).edit().clear().apply()
                        Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                binding.switchSaveCard.isChecked = false
                Toast.makeText(this, Constants.COMPLETED_ALL_DATA, Toast.LENGTH_SHORT).show()
            }
        }

        binding.txtCardnumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isEmpty()) {
                    binding.txtCvv.isEnabled = true
                }
            }

            override fun afterTextChanged(s: Editable) {
                val textCard: String = binding.txtCardnumber.text.toString()
                if (s.isEmpty()) {
                    binding.tilCardnumber.boxStrokeColor =
                        resources.getColor(R.color.design_default_color_error)
                }
                if (validation!!.luhn(textCard)) {
                    binding.tilCardnumber.boxStrokeColor = resources.getColor(R.color.green)

                } else {
                    binding.tilCardnumber.boxStrokeColor =
                        resources.getColor(R.color.design_default_color_error)
                }
                val cvv = validation!!.bin(textCard, binding.kindCard,binding.typeCard)
                if (cvv > 0) {
                    binding.txtCvv.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(cvv))
                    binding.txtCvv.isEnabled = true
                } else {
                    binding.txtCvv.isEnabled = false
                    binding.txtCvv.setText("")
                }
            }
        })

        val adapterSpinnerMonth = ArrayAdapter(
            this,R.layout.custom_spinner, months
        )
        binding.spinnerMonth.adapter = adapterSpinnerMonth

        binding.spinnerMonth.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                month = months[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        val adapterSpinnerYear = ArrayAdapter(
            this,R.layout.custom_spinner, years
        )
        binding.spinnerYear.adapter = adapterSpinnerYear

        binding.spinnerYear.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                year = years[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        if (sharedPreferencesCard.contains(Constants.KEY_CARD)){

            val type = object : TypeToken<Card>() {}.type
            val jsonReceived = sharedPreferencesCard.getString(Constants.KEY_CARD, null)
            cardData = gson.fromJson(jsonReceived, type)
            binding.txtCardnumber.setText(cardData.card_number)
            for (i in 0 until binding.spinnerMonth.adapter.count) {
                if (binding.spinnerMonth.adapter.getItem(i).toString().contains(cardData.expiration_month)) {
                    binding.spinnerMonth.setSelection(i)
                    month = cardData.expiration_month
                }
            }
            for (i in 0 until binding.spinnerYear.adapter.count) {
                if (binding.spinnerYear.adapter.getItem(i).toString().contains(cardData.expiration_year.toString())) {
                    binding.spinnerYear.setSelection(i)
                    year = cardData.expiration_year.toString()
                }
            }
            binding.txtCvv.setText(cardData.cvv)
            binding.txtEmail.setText(cardData.email)
            binding.switchSaveCard.isChecked = true
        }

        binding.btnPay.setOnClickListener {

            if (binding.txtCvv.text!!.isNotEmpty() && binding.txtCardnumber.text!!.isNotEmpty() && binding.txtEmail.text!!.isNotEmpty()){
                dialog.show()

                cardData = Card(
                    binding.txtCardnumber.text.toString(),
                    binding.txtCvv.text.toString(),
                    month,
                    year.toInt(),
                    binding.txtEmail.text.toString()
                )

                if (sharedPreferencesCard.contains(Constants.KEY_CARD)){
                    val json = gson.toJson(cardData)
                    editorCard.putString(Constants.KEY_CARD, json)
                    editorCard.apply()
                }

                val token = TokenCulqi(Constants.PUBLIC_API_KEY)
                token.createToken(applicationContext, cardData, object : TokenCallback {
                    override fun onSuccess(token: JSONObject?) {
                        try {
                            Toast.makeText(this@CheckoutActivity,"Token creado! , realizando cobro...",Toast.LENGTH_SHORT).show()
                            tokenID = token?.get(Constants.ID).toString()
                        } catch (ex: Exception) {
                            dialog.dismiss()
                            Toast.makeText(
                                this@CheckoutActivity,
                                Constants.ERROR,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        //showProgressDialog(resources.getString(R.string.success_card))
                        val amount = binding.cantToPay.text.toString().replace(".", "").toInt()
                        val dataCharge = DataCharge(
                            amount,
                            Constants.TYPE_MONEY,
                            binding.txtEmail.text.toString(),
                            tokenID!!
                        )

                      val charge = Charge(Constants.PRIVATE_API_KEY)
                        charge.createCharge(
                            applicationContext,
                            dataCharge,
                            object : ChargeCallback {
                                override fun onSuccess(charge: JSONObject?) {
                                    try {

                                    } catch (ex: Exception) {
                                        Toast.makeText(
                                            this@CheckoutActivity,
                                            Constants.ERROR_CARD,
                                            Toast.LENGTH_LONG
                                        ).show()
                                        dialog.dismiss()
                                    }
                                    DataProviderNetwork().updtateStatus(student.rfid).addOnCompleteListener{ task ->
                                        if (task.isSuccessful){
                                            val student = TinyDB(this@CheckoutActivity).getObject(Constants.KEY,Student::class.java)
                                            student.status = "Pagado"
                                            student.timestamp = System.currentTimeMillis()
                                            TinyDB(this@CheckoutActivity).putObject(Constants.KEY,student)
                                            dialog.dismiss()
                                            Toast.makeText(
                                                this@CheckoutActivity,
                                                Constants.PAY,
                                                Toast.LENGTH_LONG
                                            ).show()
                                            finish()
                                        }
                                    }.addOnFailureListener {
                                        dialog.dismiss()
                                        Toast.makeText(
                                            this@CheckoutActivity,
                                            "Error al actualizar datos"+" Usuario pagado!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        finish()
                                    }.addOnCanceledListener {
                                        dialog.dismiss()
                                        Toast.makeText(
                                            this@CheckoutActivity,
                                            "Error al actualizar datos"+" Usuario pagado!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        finish()
                                    }
                                }

                                override fun onError(error: Exception?) {
                                    dialog.dismiss()
                                    Toast.makeText(
                                        this@CheckoutActivity,
                                        Constants.ERROR_CARD,
                                        Toast.LENGTH_LONG
                                    ).show()
                                    //hideProgressDialog()
                                    //dialogPayWithCard!!.dismiss()
                                    //initPayment()
                                }
                            })

                    }

                    override fun onError(error: Exception?) {
                        Log.d("TAG","error", error)
                        dialog.dismiss()
                        //dialogPayWithCard!!.dismiss()
                        Toast.makeText(
                            this@CheckoutActivity,
                            Constants.ERROR_SERVER,
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                        //hideProgressDialog()
                    }
                })
            }else{
                Toast.makeText(
                    this@CheckoutActivity,
                    Constants.COMPLETED_ALL_DATA,
                    Toast.LENGTH_LONG
                ).show()
            }

        }


    }


}