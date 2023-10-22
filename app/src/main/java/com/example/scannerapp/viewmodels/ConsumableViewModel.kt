package com.example.scannerapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.scannerapp.database.AppDatabase
import com.example.scannerapp.database.entities.Consumable
import com.example.scannerapp.exceptions.ActiveStatusException
import com.example.scannerapp.exceptions.ItemCodeExistException
import com.example.scannerapp.exceptions.EnumValueDoesNotMatch
import com.example.scannerapp.exceptions.FieldCannotBeEmptyException
import com.example.scannerapp.exceptions.InsufficientQuantityException
import com.example.scannerapp.repository.ConsumableRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/*
The ViewModel is a part of the Android Architecture Components.
It's designed to store and manage UI-related data so that the data survives configuration changes such as screen rotations.
 */
class ConsumableViewModel(application: Application) : AndroidViewModel(application) {

  val allConsumables: LiveData<List<Consumable>>
  val allActiveConsumables: LiveData<List<Consumable>>
  private val consumableRepository: ConsumableRepository
  val selectedConsumable = MutableLiveData<Consumable?>()
  val errorLiveData = MutableLiveData<String>() // To pass error message to UI

  init {
    val consumableDao = AppDatabase.getDatabase(application).consumablesDao()
    consumableRepository = ConsumableRepository(consumableDao)
    allConsumables = consumableRepository.getAllConsumables
    allActiveConsumables = consumableRepository.getAllActiveConsumables
  }

  private fun handleException(e: Exception) {
    when (e) {
      is ItemCodeExistException,
      is FieldCannotBeEmptyException,
      is InsufficientQuantityException,
      is ActiveStatusException,
      is EnumValueDoesNotMatch -> errorLiveData.postValue(e.message)

      else -> errorLiveData.postValue("An unknown error occurred")
    }
  }

  fun addConsumable(consumable: Consumable) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        consumableRepository.addConsumable(consumable)
      } catch (e: Exception) {
        handleException(e)
      }
    }
  }

  fun updateConsumable(updatedConsumable: Consumable) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        consumableRepository.updateConsumable(updatedConsumable)
      } catch (e: Exception) {
        handleException(e)
      }
    }
  }

  fun deleteConsumable(consumableToDelete: Consumable) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        consumableRepository.deleteConsumable(consumableToDelete)
      } catch (e: Exception) {
        handleException(e)
      }
    }
  }

  fun getConsumableById(consumableId: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        consumableRepository.getConsumableById(consumableId)
      } catch (e: Exception) {
        handleException(e)
      }
    }
  }

  fun getConsumableByItemCode(itemCode: String) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        consumableRepository.getConsumableByItemCode(itemCode)
      } catch (e: Exception) {
        handleException(e)
      }
    }
  }

  suspend fun getAllBatchesQuantityRemaining(consumableId: Int): Int {
    return consumableRepository.getAllBatchesQuantityRemaining(consumableId)
  }
  // More functions...


}
