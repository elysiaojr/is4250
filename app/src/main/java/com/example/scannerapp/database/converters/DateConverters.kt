package com.example.scannerapp.database.converters

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.Date

// Converting Enums to String, vice versa
class DateConverters {
  @TypeConverter
  fun fromExpiryDate(date: Date): String {
    val df = SimpleDateFormat("dd/MM/yyyy")
    return df.format(date)
  }

  @TypeConverter
  fun toExpiryDate(dateString: String): Date? {
    val df = SimpleDateFormat("dd/MM/yyyy")
    return df.parse(dateString)
  }

//  @TypeConverter
//  fun fromTransactionDate(date: DateTime): String {
//    val df = SimpleDateFormat("dd/MM/yyyy HH:mm")
//    return df.format(date)
//  }
//
//  @TypeConverter
//  fun toTransactionDate(dateString: String): Date {
//    val df = SimpleDateFormat("dd/MM/yyyy HH:mm")
//    return df.parse(dateString)
//  }
}
