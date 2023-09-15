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
    recordDao.addRecord(record)
  }

  // More functions...
}
