package com.example.scannerapp.repository

import androidx.lifecycle.LiveData
import com.example.scannerapp.database.dao.BatchDetailsDao
import com.example.scannerapp.database.dao.RecordDao
import com.example.scannerapp.database.entities.BatchDetails
import com.example.scannerapp.database.entities.Record
import com.example.scannerapp.database.entities.RecordType
import com.example.scannerapp.exceptions.*

class RecordRepository(
  private val recordDao: RecordDao,
  private val batchDetailsDao: BatchDetailsDao
) {

  val getAllRecords: LiveData<List<Record>> = recordDao.getAllRecords()
  val getAllBatchDetails: LiveData<List<BatchDetails>> = batchDetailsDao.getAllBatchDetails()

  private suspend fun adjustBatchDetails(record: Record) {
    val batchDetails = batchDetailsDao.getBatchDetailById(record.batchId)

    when (record.recordType) {
      RecordType.TAKE_OUT -> {
        val newRemainingQuantity =
          batchDetails.batchRemainingQuantity - record.recordQuantityChanged
        if (newRemainingQuantity < 0) {
          throw InsufficientQuantityException("Not enough quantity in batch")
        }
        batchDetailsDao.update(batchDetails.copy(batchRemainingQuantity = newRemainingQuantity))
      }

      RecordType.PUT_IN -> {
        val newRemainingQuantity =
          batchDetails.batchRemainingQuantity + record.recordQuantityChanged
        batchDetailsDao.update(batchDetails.copy(batchRemainingQuantity = newRemainingQuantity))
      }

      else -> throw InvalidRecordTypeException("Invalid Record Type")
    }
  }

  suspend fun addRecord(record: Record): Boolean {
    return try {
      validateRecord(record)
      adjustBatchDetails(record)
      recordDao.insert(record)
      true
    } catch (e: Exception) {
      println("in add record (AT REPOSITORY): " + e.message)
      throw e
    }
  }

  suspend fun updateRecord(record: Record) {
    recordDao.update(record)
  }

  suspend fun deleteRecord(record: Record) {
    recordDao.delete(record)
  }

  suspend fun getRecordById(recordId: Int): Record {
    return recordDao.getRecordById(recordId)
  }

  private fun validateRecord(record: Record) {
    if (record.recordDate.trim().isEmpty()) {
      throw FieldCannotBeEmptyException("Record Date field cannot be empty")
    }

    if (record.recordQuantityChanged <= 0) {
      throw InsufficientQuantityException("Quantity Changed must be at least 1")
    }

    if (record.recordType !in RecordType.values()) {
      throw EnumValueDoesNotMatch("Invalid record type")
    }

    if (record.isActive != 0 && record.isActive != 1) {
      throw ActiveStatusException("Active field can only be active (1) or not active (0)")
    }
  }

  // More functions...
}
