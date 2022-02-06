package com.osho.powerweight.Utils

import com.osho.powerweight.UI.HealthActivity

class IMC : HealthActivity() {

    fun valueIMC(imc: Double): String {
      if (imc <= 15.0) {
          return  Constants.DEL_MUY_SEVERA
        } else if (imc > 15.0 && imc < 15.9) {
          return  Constants.DEL_SEVERA
        }  else if (imc > 16.0 && imc < 18.4) {
          return  Constants.DELGADEZ
        } else if (imc > 18.5 && imc < 24.9) {
          return  Constants.PESO_SALUDABLE
        } else if (imc > 25.0 && imc < 29.9) {
          return  Constants.SOBRE_PESO
        } else if (imc > 30.0 && imc < 34.9) {
          return  Constants.OB_MODERADA
        } else if (imc > 35.0 && imc < 39.9) {
          return  Constants.OB_SEVERA
        }
      else {
          return Constants.OB_MUY_SEVERA
        }
    }

}