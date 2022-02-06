package com.osho.powerweight.Utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {

    const val CREDENTIALS = "credentials"
    const val KEY_CREDENTIALS = "key_credentials"
    const val USERS = "users"
    const val STUDENTS = "students"
    const val DNI = "dni_student"
    const val USER_DATA = "user_data"
    const val IMAGE_PROFILE = "image_profile"
    const val USER_CARD_DATA = "CARD_DATA"
    const val KEY_CARD = "key_card"
    const val COMPLETED_ALL_DATA = "Complete todos los datos!"
    const val WEIGHT_LIST = "weight_list"
    const val WEIGHT_LIST_NORMAL = "weight_list_normal"
    const val KEY = "key"
    const val REGISTERS = "registers"
    const val CODE_STUDENT = "code_student"
    const val SUCCESS = "success"
    const val KEY_SUCCESS = "key_success"
    const val SIGN_IN = "sign_in"
    const val PROFILE_COMPLETED = "profile_completed"
    const val KEY_PROFILE = "key_profile"
    const val SUCCESS_PROFILE_COMPLETED = "1"
    const val GET_LAST_WEIGHT = "get_last_weight"
    const val KEY_WEIGHT = "key_weight"
    const val DEL_MUY_SEVERA = "Delgadez muy severa"
    const val DEL_SEVERA = "Delgadez severa"
    const val DELGADEZ = "Delgadez"
    const val PESO_SALUDABLE = "Peso saludable"
    const val SOBRE_PESO = "Sobre Peso"
    const val OB_MODERADA = "Obesidad moderada"
    const val OB_SEVERA= "Obesidad severa"
    const val OB_MUY_SEVERA= "Obesidad muy severa"
    const val RFID = "rfid"
    const val DIET_DELGADEZ = "•Comer con mayor frecuencia: es posible que el paciente se sienta satisfecho más rápido, si tiene bajo peso. Se recomienda comer 5 o 6 veces diarias con menor cantidad, en lugar de 2 o 3 grandes comidas.\n" +
            "•Licuados y batidos: las bebidas saludables con leche y fruta, con algo de lianza molida, son altamente aconsejables. Por el contrario, no se recomienda el café, refrescos dietéticos u otras bebidas con pocas calorías o valor nutricional.\n" +
            "•Alimentos ricos en nutrientes: pan, pasta, cereales integrales, vegetales y frutas, así como lácteos, fuentes de proteínas magra y nueces y semillas.\n" +
            "•Controlar los líquidos: a veces los líquidos antes de las comidas pueden reducir el apetito. En tal caso, se recomienda tomar bebidas con alto contenido calórico, junto a una comida. También se puede beber media hora tras comer.\n" +
            "•Agregar extras: añadir ingredientes como queso en los guisos y huevos revueltos o leche en polvo descremada en sopas, añadirá un mayor número de calorías.\n" +
            "•Practicar ejercicio: especialmente el fortalecimiento muscular, es útil para aumentar de peso al fortalecer los músculos. Asimismo, también estimulará el apetito.\n" +
            "•Permitirnos un gusto: incluso las personas con bajo peso deben vigilar los excesos de grasa y azúcar. Las magdalenas de salvado, las barritas de granola o el yogurt son buenas opciones."
    const val CAUSE_DELGADEZ = "•Depresión, estrés o ansiedad\n" +
            "•Cáncer\n" +
            "•Enfermedades o infecciones crónicas\n" +
            "•Consumo excesivo de fármacos\n" +
            "•Consumo de drogas ilícitas\n"+
            "•Problemas de fertilidad, especialmente en mujeres. También problemas durante el embarazo.\n" +
            "•Anemia\n" +
            "•Pérdida del cabello\n" +
            "•Mayor riesgo de sufrir osteoporosis"
    const val DIET_OBESIDAD = "•Lácteos: La leche y los yogures serán desnatados; los quesos, magros. Se aconsejan 2-3 raciones al día para asegurar un aporte adecuado de calcio.\n" +
            "•Carnes y pescados: Se seleccionarán cortes magros y se retirará la grasa visible antes de la cocción. En el caso de las aves, quitar toda la piel. Evitaremos alimentos ricos en grasa saturada, como los embutidos y el tocino. Se recomienda consumir pescado al menos tres veces a la semana.\n" +
            "•Huevos: Es un alimento muy completo que presenta una proteína de alto valor biológico. Se incluirán preferentemente cocido o pasados por agua y se evitará la fritura.\n" +
            "•Cereales legumbres y tubérculos: Aportan principalmente hidratos de carbono complejos, muy útiles en el control del apetito porque aumentan la sensación de saciedad. De este grupo, se restringirá la bollería (croissants, ensaimadas, magdalenas, donuts, etcétera).\n" +
            "•Frutas y verduras: Elige frutas crudas de consistencia firme, evitando las piezas cocidas o los batidos que aportan menor saciedad. Se recomienda un alto consumo de verduras y hortalizas. Lo ideal es consumir al menos cinco raciones diarias entre frutas y verduras.\n" +
            "•Grasa y aceites: Utiliza métodos de cocción que precisen poca grasa (plancha, horno, papillote), reduciendo el uso de frituras, salsas grasas y rebozados. Se recomienda el uso del aceite de oliva virgen extra.\n" +
            "•Azúcar y derivados: Restringir los alimentos con alta concentración de azúcares: azúcar, mermelada y refrescos. En su lugar, utiliza edulcorantes y refrescos dietéticos que no aporten calorías."
    const val DIET_NORMAL = "Felicitaciones!, continua con tu actual alimentacion!"
    const val CAUSE_OBESIDAD = "•Enfermedad coronaria\n" +
            "•Diabetes tipo 2\n" +
            "•Cáncer (de endometrio, de mama y de colon)\n" +
            "•Hipertensión (presión arterial alta)\n" +
            "•Dislipidemia (por ejemplo, niveles altos de colesterol total o de triglicéridos)\n" +
            "•Accidente cerebrovascular\n" +
            "•Enfermedad del hígado y de la vesícula\n" +
            "•Apnea del sueño y problemas respiratorios\n" +
            "•Artrosis (la degeneración del cartílago y el hueso subyacente de una articulación)\n" +
            "•Problemas ginecológicos (menstruación anómala, infertilidad)\n" +
            "*El sobrepeso se define como un índice de masa corporal (IMC) de 25 o más y la obesidad como un IMC de 30 o más."
    const val ACTUAL_DATE_TIME = "actual_date_time"
    const val KEY_ACTUAL_DATE_TIME = "key_actual_date_time"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 2

    const val PUBLIC_API_KEY = "pk_live_c84HQbHmA05kP1hr"
    const val PRIVATE_API_KEY = "sk_live_pRtJrGvZC01P7zEg"
    const val SUCCESS_TOKEN = "Token creado !"
    const val SUCCESS_CHARGE = "CARGO CREADO"
    const val ERROR = "Error"
    const val ERROR_CARD = "Error de tarjeta ó saldo insuficiente"
    const val ERROR_SERVER = "Error de servidor, intentelo más tarde..."
    const val ADD_AMOUNT = "Agregue el monto"
    const val MINOR_AMOUNT = "El monto debe ser mayor a 3 soles"
    const val TYPE_MONEY = "PEN"
    const val VALIDATING_INFO_CARD = "Validando informacion de la tarjeta"
    const val RUN_CHARGE = "Realizando cargo"
    const val CARD_NUMBER = "card_number"
    const val CVV = "cvv"
    const val EXPIRATION_MONTH = "expiration_month"
    const val EXPIRATION_YEAR = "expiration_year"
    const val EMAIL = "email"
    const val ID = "id"
    const val URL_BASE_SECURE = "https://secure.culqi.com/v2/"
    const val URL_BASE = "https://api.culqi.com/v2/"
    const val URL_TOKENS = "tokens/"
    const val URL_CHARGES = "charges/"
    const val HEADERS_CONTENT_TYPE = "Content-Type"
    const val CONTENT_TYPE = "application/json"
    const val HEADERS_AUTHORIZATION = "Authorization"
    const val AUTHORIZATION = "Bearer "
    const val INITIAL_TIMEOUTS_MS = 30000
    const val AMOUNT = "amount"
    const val CURRENCY_CODE = "currency_code"
    const val SOURCE_ID = "source_id"
    const val VISA = "Visa"
    const val MASTERCARD = "MasterCard"
    const val DINERS_CLUB = "Diners Club"
    const val AMEX = "Amex"
    const val PAY = "PAGADO!"
    const val MALE = "Masculino"
    const val FEMALE = "Femenino"
    const val CULQUI_PASSWORD = "12345678Mm@"


    fun showImageChooser(activity: Activity) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    /**
     * A function to get the image file extension of the selected image.
     *
     * @param activity Activity reference.
     * @param uri Image file uri.
     */
    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        /*
         * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
         *
         * getSingleton(): Get the singleton instance of MimeTypeMap.
         *
         * getExtensionFromMimeType: Return the registered extension for the given MIME type.
         *
         * contentResolver.getType: Return the MIME type of the given content URL.
         */
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

}