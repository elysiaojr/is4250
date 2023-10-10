package com.example.scannerapp.repository

import androidx.lifecycle.LiveData
import com.example.scannerapp.database.dao.BatchDetailsDao
import com.example.scannerapp.database.dao.RecordDao
import com.example.scannerapp.database.entities.BatchDetails
import com.example.scannerapp.database.entities.Record
import com.example.scannerapp.database.entities.RecordType
import com.example.scannerapp.exceptions.InsufficientQuantityException
import java.lang.IllegalStateException

/*
Repositories are responsible for abstracting the source of data for your app.
Most business logic are here.
 */
class RecordRepository(
  private val recordDao: RecordDao,
  private val batchDetailsDao: BatchDetailsDao
) {

  val getAllRecords: LiveData<List<Record>> = recordDao.getAllRecords()
  val getAllBatchDetails: LiveData<List<BatchDetails>> = batchDetailsDao.getAllBatchDetails()


  suspend fun addRecord(record: Record) {
    try {
      // Get current BatchDetails
      val batchDetails = batchDetailsDao.getBatchDetailById(record.batchId)

      // Modify BatchDetails based on RecordType
      if (record.recordType == RecordType.TAKE_OUT) {
        val newRemainingQuantity =
          batchDetails.batchRemainingQuantity - record.recordQuantityChanged
        if (newRemainingQuantity < 0) {
          throw InsufficientQuantityException("Not enough quantity in batch")
        }
        batchDetailsDao.update(batchDetails.copy(batchRemainingQuantity = newRemainingQuantity))
      } else if (record.recordType == RecordType.PUT_IN) {
        val newRemainingQuantity =
          batchDetails.batchRemainingQuantity + record.recordQuantityChanged
        // Maybe can check that it does not exceed initial batch quantity before updating
        batchDetailsDao.update(batchDetails.copy(batchRemainingQuantity = newRemainingQuantity))
      } else {
        // Handle error
        throw IllegalStateException("Invalid Record Type")
      }
      // Insert record last
      recordDao.insert(record)

    } catch (e: Exception) {
      // Handle error appropriately
    }
  }

  // For soft deletion, use this
  suspend fun updateRecord(record: Record) {
    recordDao.update(record)
  }

  suspend fun deleteRecord(record: Record) {
    recordDao.delete(record)
  }

  suspend fun getRecordById(recordId: Int): Record {
    return recordDao.getRecordById(recordId)
  }

  // More functions...
}
