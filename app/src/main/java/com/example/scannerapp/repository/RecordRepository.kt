package com.example.scannerapp.repository

import androidx.lifecycle.LiveData
import com.example.scannerapp.database.dao.BatchDetailsDao
import com.example.scannerapp.database.dao.RecordDao
import com.example.scannerapp.database.entities.BatchDetails
import com.example.scannerapp.database.entities.Record
import com.example.scannerapp.database.entities.RecordType
import java.lang.IllegalStateException

/*
Repositories are responsible for abstracting the source of data for your app.
Most business logic are here.
 */
class RecordRepository(private val recordDao: RecordDao, private val batchDetailsDao: BatchDetailsDao) {

  val getAllRecords: LiveData<List<Record>> = recordDao.getAllRecords()
  val getAllBatchDetails: LiveData<List<BatchDetails>> = batchDetailsDao.getAllBatchDetails()


  suspend fun addRecord(record: Record) {

    if (record.recordType.equals(RecordType.TAKE_OUT)) {
      val quantityTaken = record.recordQuantityChanged
      // Update quantity
      val batchDetails: BatchDetails = batchDetailsDao.getBatchDetailById(record.batchId)
      val remainingQuantity = batchDetails.batchRemainingQuantity - quantityTaken

      // Updating if the remainingQuantity is valid
      if (remainingQuantity >= 0) {
        batchDetailsDao.updateBatchRemainingQuantity(record.batchId, remainingQuantity)
        recordDao.insert(record)
      } else {
//        throw IllegalStateException("Remaining quantity cannot be less than zero!")
      }
    } else if (record.recordType.equals(RecordType.PUT_IN)) {

      val quantityAdded = record.recordQuantityChanged
      // Update quantity
      val batchDetails: BatchDetails = batchDetailsDao.getBatchDetailById(record.batchId)
      val remainingQuantity = batchDetails.batchRemainingQuantity + quantityAdded

      // Updating the remainingQuantity
      batchDetailsDao.updateBatchRemainingQuantity(record.batchId, remainingQuantity)
      recordDao.insert(record)

    } else {
      // Catch error
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
