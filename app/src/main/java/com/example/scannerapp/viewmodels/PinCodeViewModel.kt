package com.example.scannerapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scannerapp.database.AppDatabase
import com.example.scannerapp.database.entities.PinCode
import com.example.scannerapp.database.entities.Record
import com.example.scannerapp.database.entities.User
import com.example.scannerapp.exceptions.ActiveStatusException
import com.example.scannerapp.exceptions.EnumValueDoesNotMatch
import com.example.scannerapp.exceptions.FieldCannotBeEmptyException
import com.example.scannerapp.exceptions.IncorrectPinLengthException
import com.example.scannerapp.exceptions.InsufficientQuantityException
import com.example.scannerapp.exceptions.InvalidPinCodeException
import com.example.scannerapp.exceptions.InvalidRecordTypeException
import com.example.scannerapp.repository.PinCodeRepository
import com.example.scannerapp.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PinCodeViewModel(application: Application) : AndroidViewModel(application) {

  private val pinCodeRepository: PinCodeRepository
  val errorLiveData = MutableLiveData<String>() // To pass error message to UI
  val editSuccessLiveData = MutableLiveData<String>() // To pass error message to UI

  init {
    val pinCodeDao = AppDatabase.getDatabase(application).pinCodeDao()
    pinCodeRepository = PinCodeRepository(pinCodeDao)
  }

  private fun handleException(e: Exception) {
    when (e) {
      is IncorrectPinLengthException,
      is FieldCannotBeEmptyException,
      is InvalidPinCodeException -> {
        errorLiveData.postValue(e.message)
      }

      else -> errorLiveData.postValue("An unknown error occurred")
    }
  }

  fun updatePinCode(pinCode: PinCode, oldPinCode: String) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        pinCodeRepository.updatePinCode(pinCode, oldPinCode)
        editSuccessLiveData.postValue("Pin Code updated.")
      } catch (e: Exception) {
        handleException(e)
      }
    }
  }
  suspend fun getPinCode(): String {
    return pinCodeRepository.getPinCode()
  }


  // ... other ViewModel methods related to the PinCode functionality

}
