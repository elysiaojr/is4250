package com.example.scannerapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.scannerapp.database.AppDatabase
import com.example.scannerapp.database.entities.Record
import com.example.scannerapp.repository.RecordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecordViewModel(application: Application) : AndroidViewModel(application) {

  val allRecords: LiveData<List<Record>>
  private val recordRepository: RecordRepository

  init {
    val recordDao = AppDatabase.getDatabase(application).recordDao()
    recordRepository = RecordRepository(recordDao)
    allRecords = recordRepository.getAllRecords
  }

  fun addRecord(record: Record) {
    viewModelScope.launch(Dispatchers.IO) {
      recordRepository.addRecord(record)
    }
  }

  // More functions...
}
