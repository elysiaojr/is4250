package com.example.scannerapp.repository

import com.example.scannerapp.database.dao.PinCodeDao
import com.example.scannerapp.database.entities.PinCode
import com.example.scannerapp.database.entities.Record
import com.example.scannerapp.database.entities.RecordType
import com.example.scannerapp.exceptions.ActiveStatusException
import com.example.scannerapp.exceptions.EnumValueDoesNotMatch
import com.example.scannerapp.exceptions.FieldCannotBeEmptyException
import com.example.scannerapp.exceptions.IncorrectPinLengthException
import com.example.scannerapp.exceptions.InsufficientQuantityException
import com.example.scannerapp.exceptions.InvalidPinCodeException
import com.example.scannerapp.exceptions.InvalidRecordTypeException

class PinCodeRepository(private val pinCodeDao: PinCodeDao) {

  // Fetches hashed pin from the database
  suspend fun getPinCode(): String {
    return pinCodeDao.getPinCode()
  }

  suspend fun updatePinCode(pinCode: PinCode, oldPinCode: String) {
    try {
      validatePinCode(pinCode, oldPinCode)
      pinCodeDao.update(pinCode)
    } catch (e: Exception) {
      throw e
    }
  }

  suspend fun validatePinCode(pinCode: PinCode, oldPinCode: String) {
    if (pinCodeDao.verifyPinCode(oldPinCode) != 1) {
      throw InvalidPinCodeException("Old Pin Code is invalid.")
    }
    if (pinCode.pinCode.length != 4) {
      throw IncorrectPinLengthException("Pin Code must have 4 digits.")
    }

    if (pinCode.pinCode.trim().isEmpty()) {
      throw FieldCannotBeEmptyException("Pin Code field cannot be empty.")
    }
  }

  // ... any other needed methods like saving or updating the pin

}
