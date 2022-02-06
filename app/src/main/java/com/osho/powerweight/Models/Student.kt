package com.osho.powerweight.Models

import java.sql.Timestamp

data class Student(val fullname : String = "",
                   val code_student : String = "",
                   val dni_student : String = "",
                   var image_profile : String ="",
                   var profile_completed : String = "",
                   val carrer : String = "",
                   val rfid : String = "",
                   val sex : String ="",
                   var age : String = "",
                   var height : String = "",
                   var timestamp : Long = 0L,
                   var status : String = "")
