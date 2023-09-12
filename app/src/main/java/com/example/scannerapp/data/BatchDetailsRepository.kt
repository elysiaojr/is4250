package com.example.scannerapp.data

import androidx.lifecycle.LiveData

/*
Repositories are responsible for abstracting the source of data for your app.
Most business logic are here.
 */
class BatchDetailsRepository(private val batchDetailsDao: BatchDetailsDao) {
  val getAllBatchDetails: LiveData<List<BatchDetails>> = batchDetailsDao.getAllBatchDetails()

  suspend fun addBatchDetails(batchDetails: BatchDetails) {
    batchDetailsDao.addBatchDetails(batchDetails)
  }

  // More functions...
}
