package com.example.scannerapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.scannerapp.database.AppDatabase
import com.example.scannerapp.database.entities.BatchDetails
import com.example.scannerapp.repository.BatchDetailsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/*
The ViewModel is a part of the Android Architecture Components.
It's designed to store and manage UI-related data so that the data survives configuration changes such as screen rotations.
 */
class BatchDetailsViewModel(application: Application) : AndroidViewModel(application) {

  val allBatches: LiveData<List<BatchDetails>>
  private val batchDetailsRepository: BatchDetailsRepository

  init {
    val batchDetailsDao = AppDatabase.getDatabase(application).batchDetailsDao()
    batchDetailsRepository = BatchDetailsRepository(batchDetailsDao)
    allBatches = batchDetailsRepository.getAllBatchDetails
  }

  fun addBatchDetails(batchDetails: BatchDetails) {
    viewModelScope.launch(Dispatchers.IO) {
      batchDetailsRepository.addBatchDetails(batchDetails)
    }
  }

  /**
   * Removes a specified quantity of items from a batch identified by its ID.
   *
   * This function allows you to reduce the quantity of items available in a batch.
   *
   * @param batchID The unique identifier of the batch from which items are being taken.
   * @param quantityTaken The quantity of items to remove from the batch.
   *
   * @see [BatchDetailsRepository.takeOutFromBatch] for the underlying data operation.
   */
  fun takeOutFromBatch(batchID: Int, quantityTaken: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      batchDetailsRepository.takeOutFromBatch(batchID, quantityTaken)
    }
  }
  // More functions...
}
