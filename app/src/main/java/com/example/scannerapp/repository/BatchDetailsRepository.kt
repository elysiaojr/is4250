package com.example.scannerapp.repository

import androidx.lifecycle.LiveData
import com.example.scannerapp.database.dao.BatchDetailsDao
import com.example.scannerapp.database.entities.BatchDetails

/*
Repositories are responsible for abstracting the source of data for your app.
Most business logic are here.
 */
class BatchDetailsRepository(private val batchDetailsDao: BatchDetailsDao) {
  val getAllBatchDetails: LiveData<List<BatchDetails>> = batchDetailsDao.getAllBatchDetails()

  suspend fun addBatchDetails(batchDetails: BatchDetails) {
    batchDetailsDao.addBatchDetails(batchDetails)
  }

  suspend fun takeOutFromBatch(batchID: Int, quantityTaken: Int) {
    val batchDetails: BatchDetails = batchDetailsDao.getBatchDetailById(batchID)
    val remainingQuantity = batchDetails.batchRemainingQuantity - quantityTaken
    batchDetailsDao.addBatchDetails(batchDetails)
  }
  // More functions...
}
