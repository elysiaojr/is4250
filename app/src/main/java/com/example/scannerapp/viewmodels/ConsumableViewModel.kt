package com.example.scannerapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.scannerapp.database.AppDatabase
import com.example.scannerapp.database.entities.Consumable
import com.example.scannerapp.repository.ConsumableRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/*
The ViewModel is a part of the Android Architecture Components.
It's designed to store and manage UI-related data so that the data survives configuration changes such as screen rotations.
 */
class ConsumableViewModel(application: Application) : AndroidViewModel(application) {

  val allConsumables: LiveData<List<Consumable>>
  private val consumableRepository: ConsumableRepository
  val selectedConsumable = MutableLiveData<Consumable?>()

  init {
    val consumableDao = AppDatabase.getDatabase(application).consumablesDao()
    consumableRepository = ConsumableRepository(consumableDao)
    allConsumables = consumableRepository.getAllConsumables
  }

  fun addConsumable(consumable: Consumable) {
    viewModelScope.launch(Dispatchers.IO) {
      consumableRepository.addConsumable(consumable)
    }
  }

  fun getConsumable(consumableId: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      consumableRepository.getConsumable(consumableId)
    }
  }

  fun updateConsumable(updatedConsumable: Consumable) {
    viewModelScope.launch(Dispatchers.IO) {
      consumableRepository.updateConsumable(updatedConsumable)
    }
  }


  // More functions...
}
