package com.example.scannerapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pin_code")
data class PinCode(
  @PrimaryKey
  val id: Int = 1,  // Assuming you'll have only one row for the app's pin code
  val pinCode: String
)
