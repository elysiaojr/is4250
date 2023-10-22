package com.example.scannerapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.scannerapp.database.AppDatabase
import com.example.scannerapp.database.entities.User
import com.example.scannerapp.repository.PinCodeRepository
import com.example.scannerapp.repository.UserRepository

class PinCodeViewModel(application: Application) : AndroidViewModel(application) {

  private val pinCodeRepository: PinCodeRepository
  val errorLiveData = MutableLiveData<String>()

  init {
    val pinCodeDao = AppDatabase.getDatabase(application).pinCodeDao()
    pinCodeRepository = PinCodeRepository(pinCodeDao)
  }

  suspend fun getPinCode(): String {
    return pinCodeRepository.getPinCode()
  }

  // ... other ViewModel methods related to the PinCode functionality

}
