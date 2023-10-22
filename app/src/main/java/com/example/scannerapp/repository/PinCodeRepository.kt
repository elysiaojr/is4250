package com.example.scannerapp.repository

import com.example.scannerapp.database.dao.PinCodeDao
import com.example.scannerapp.database.entities.PinCode

class PinCodeRepository(private val pinCodeDao: PinCodeDao) {

  // Fetches hashed pin from the database
  suspend fun getPinCode(): String {
    return pinCodeDao.getPinCode()
  }

  // ... any other needed methods like saving or updating the pin

}
