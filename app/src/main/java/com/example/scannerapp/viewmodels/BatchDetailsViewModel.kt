package com.example.scannerapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.scannerapp.database.AppDatabase
import com.example.scannerapp.database.entities.BatchDetails
import com.example.scannerapp.database.entities.Consumable
import com.example.scannerapp.database.entities.UnitOfMeasurement
import com.example.scannerapp.exceptions.ActiveStatusException
import com.example.scannerapp.exceptions.BatchNumberExistException
import com.example.scannerapp.exceptions.ExpiryDateBeforeCreateDate
import com.example.scannerapp.exceptions.FieldCannotBeEmptyException
import com.example.scannerapp.exceptions.InsufficientQuantityException
import com.example.scannerapp.exceptions.RemainingQuantityExceedsReceivedQuantityException
import com.example.scannerapp.repository.BatchDetailsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/*
The ViewModel is a part of the Android Architecture Components.
It's designed to store and manage UI-related data so that the data survives configuration changes such as screen rotations.
 */
class BatchDetailsViewModel(application: Application) : AndroidViewModel(application) {

  val allBatchDetails: LiveData<List<BatchDetails>>
  val allActiveBatchDetails: LiveData<List<BatchDetails>>
  private val batchDetailsRepository: BatchDetailsRepository
  val errorLiveData = MutableLiveData<String>()
  val successLiveData = MutableLiveData<String>() // To pass error message to UI
  val editSuccessLiveData = MutableLiveData<BatchDetails>()
  val selectedBatchDetails = MutableLiveData<BatchDetails?>()
//  val allBatchDetailsByExpiryDate: LiveData<List<BatchDetails>>
//  val allBatchDetailsByConsumableNameAsc: LiveData<List<BatchDetails>>
//  val allBatchDetailsByConsumableNameDesc: LiveData<List<BatchDetails>>

  init {
    val batchDetailsDao = AppDatabase.getDatabase(application).batchDetailsDao()
    batchDetailsRepository = BatchDetailsRepository(batchDetailsDao)
    allBatchDetails = batchDetailsRepository.getAllBatchDetails
    allActiveBatchDetails = batchDetailsRepository.getAllActiveBatchDetails
//    allBatchDetailsByExpiryDate = batchDetailsRepository.getAllBatchDetailsByExpiryDate
//    allBatchDetailsByConsumableNameAsc = batchDetailsRepository.getAllBatchDetailsByConsumableNameAsc
//    allBatchDetailsByConsumableNameDesc = batchDetailsRepository.getAllBatchDetailsByConsumableNameDesc
  }

  private fun handleException(e: Exception) {
    when (e) {
      is FieldCannotBeEmptyException,
      is InsufficientQuantityException,
      is RemainingQuantityExceedsReceivedQuantityException,
      is ActiveStatusException,
      is ExpiryDateBeforeCreateDate,
      is BatchNumberExistException -> {
        errorLiveData.postValue(e.message)
      }

      else -> {
        errorLiveData.postValue("An unknown error occurred.")
      }
    }
  }

  fun addBatchDetails(batchDetails: BatchDetails) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        batchDetailsRepository.addBatchDetails(batchDetails)
        successLiveData.postValue(batchDetails.batchId.toString())
      } catch (e: Exception) {
        Log.e("BatchDetailsViewModel", "Error adding batch details", e) // Log the exception
        handleException(e)
      }
    }
  }

  fun updateBatchDetails(updatedBatchDetails: BatchDetails) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        batchDetailsRepository.updateBatchDetails(updatedBatchDetails)
        successLiveData.postValue(updatedBatchDetails.batchId.toString())
        editSuccessLiveData.postValue(updatedBatchDetails)
      } catch (e: Exception) {
        handleException(e)
      }
    }
  }

  fun deleteBatchDetails(batchDetailsToDelete: BatchDetails) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        batchDetailsRepository.deleteBatchDetails(batchDetailsToDelete)
      } catch (e: Exception) {
        handleException(e)
      }
    }
  }


  fun getBatchDetailsById(batchId: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        batchDetailsRepository.getBatchDetailsById(batchId)
      } catch (e: Exception) {
        handleException(e)
      }
    }
  }

  fun getBatchDetailsByBatchNumber(batchNumber: String) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        batchDetailsRepository.getBatchDetailsByBatchNumber(batchNumber)
      } catch (e: Exception) {
        handleException(e)
      }
    }
  }

  // Function to fetch BatchDetails by batch number
  suspend fun getBatchDetailsLiveDataByBatchNumber(batchNumber: String): BatchDetails {
    return batchDetailsRepository.getBatchDetailsLiveDataByBatchNumber(batchNumber)
  }

  suspend fun getBatchDetailUOM(consumableId: Int): UnitOfMeasurement {
    return batchDetailsRepository.getBatchDetailUOM(consumableId)
  }

  suspend fun getBatchDetailsNameById(batchId: Int): String {
    return batchDetailsRepository.getBatchDetailsNameById(batchId)
  }

  suspend fun getBatchExpiryDateById(batchId: Int): String {
    return batchDetailsRepository.getBatchExpiryDateById(batchId)
  }

  suspend fun getBatchIdByBatchNumber(batchNumber: String): Int {
    return batchDetailsRepository.getBatchIdByBatchNumber(batchNumber)
  }

  suspend fun getBatchDetailConsumableName(consumableId: Int): String {
    val nameString = batchDetailsRepository.getBatchDetailConsumableName(consumableId)
    val brandString = batchDetailsRepository.getBatchDetailConsumableBrand(consumableId)
    val typeString = batchDetailsRepository.getBatchDetailConsumableType(consumableId)
    val sizeString = batchDetailsRepository.getBatchDetailConsumableSize(consumableId)
    return "$nameString, $brandString, $typeString, $sizeString"
  }

  suspend fun getBatchDetailConsumableNameByBatchNumber(batchNumber: String): String {
    val nameString = batchDetailsRepository.getBatchDetailConsumableNameByBatchNumber(batchNumber)
    val brandString = batchDetailsRepository.getBatchDetailConsumableBrandByBatchNumber(batchNumber)
    val typeString =
      batchDetailsRepository.getBatchDetailConsumableTypeByBatchNumber(batchNumber) ?: ""
    val sizeString =
      batchDetailsRepository.getBatchDetailConsumableSizeByBatchNumber(batchNumber) ?: ""
    return "$nameString, $brandString, $typeString, $sizeString"
  }

  suspend fun checkIfBatchNumberExists(batchNumber: String): Boolean {
    return batchDetailsRepository.countOfBatchDetailsByBatchNumber(batchNumber) > 0
  }

  suspend fun getBatchesWithEarlierExpiryDates(
    date: String,
    consumableId: Int
  ): List<BatchDetails> {
    return batchDetailsRepository.getBatchesWithEarlierExpiryDates(date, consumableId)
  }
  // More functions...

}
