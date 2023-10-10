package com.example.scannerapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.scannerapp.database.AppDatabase
import com.example.scannerapp.database.entities.BatchDetails
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
  private val batchDetailsRepository: BatchDetailsRepository
  val errorLiveData = MutableLiveData<String>()

  init {
    val batchDetailsDao = AppDatabase.getDatabase(application).batchDetailsDao()
    batchDetailsRepository = BatchDetailsRepository(batchDetailsDao)
    allBatchDetails = batchDetailsRepository.getAllBatchDetails
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
      } catch (e: Exception) {
        handleException(e)
      }
    }
  }

  fun updateBatchDetails(updatedBatchDetails: BatchDetails) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        batchDetailsRepository.updateBatchDetails(updatedBatchDetails)
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

  suspend fun getBatchDetailUOM(consumableId: Int): UnitOfMeasurement {
    return batchDetailsRepository.getBatchDetailUOM(consumableId)
  }

  // More functions...
}
