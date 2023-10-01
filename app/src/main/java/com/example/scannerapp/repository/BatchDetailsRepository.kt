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
    batchDetailsDao.insert(batchDetails)
  }

  // For soft deletion, use this
  suspend fun updateBatchDetails(batchDetails: BatchDetails) {
    batchDetailsDao.update(batchDetails)
  }

  suspend fun deleteBatchDetails(batchDetails: BatchDetails) {
    batchDetailsDao.delete(batchDetails)
  }

  suspend fun getBatchDetailsById(batchId: Int): BatchDetails {
    return batchDetailsDao.getBatchDetailById(batchId)
  }

  // More functions...

}
