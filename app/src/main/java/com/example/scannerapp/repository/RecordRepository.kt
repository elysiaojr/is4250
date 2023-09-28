package com.example.scannerapp.repository

import androidx.lifecycle.LiveData
import com.example.scannerapp.database.dao.RecordDao
import com.example.scannerapp.database.entities.Record

/*
Repositories are responsible for abstracting the source of data for your app.
Most business logic are here.
 */
class RecordRepository(private val recordDao: RecordDao) {

  val getAllRecords: LiveData<List<Record>> = recordDao.getAllRecords()

  suspend fun addRecord(record: Record) {
    recordDao.insert(record)
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
