package com.example.scannerapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.scannerapp.database.AppDatabase
import com.example.scannerapp.database.entities.Record
import com.example.scannerapp.exceptions.ActiveStatusException
import com.example.scannerapp.exceptions.EnumValueDoesNotMatch
import com.example.scannerapp.exceptions.FieldCannotBeEmptyException
import com.example.scannerapp.exceptions.InsufficientQuantityException
import com.example.scannerapp.exceptions.InvalidRecordTypeException
import com.example.scannerapp.repository.RecordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecordViewModel(application: Application) : AndroidViewModel(application) {

  val allRecords: LiveData<List<Record>>
  private val recordRepository: RecordRepository
  val errorLiveData = MutableLiveData<String>() // To pass error message to UI
  val successLiveData = MutableLiveData<String>() // To pass error message to UI

  init {
    val recordDao = AppDatabase.getDatabase(application).recordDao()
    val batchDetailsDao = AppDatabase.getDatabase(application).batchDetailsDao()
    recordRepository = RecordRepository(recordDao, batchDetailsDao)
    allRecords = recordRepository.getAllRecords
  }

  private fun handleException(e: Exception) {
    when (e) {
      is InsufficientQuantityException,
      is InvalidRecordTypeException,
      is FieldCannotBeEmptyException,
      is ActiveStatusException,
      is EnumValueDoesNotMatch -> {
        errorLiveData.postValue(e.message)
      }

      else -> errorLiveData.postValue("An unknown error occurred")
    }
  }

  fun addRecord(record: Record) {
    println("in add record: " +  record.batchId)
    viewModelScope.launch(Dispatchers.IO) {
      try {
        println("in add record, try: " +  record.batchId)
        recordRepository.addRecord(record)
        successLiveData.postValue(record.batchId.toString())
      } catch (e: Exception) {
        println("in add record, exception: " +  e.message)
        handleException(e)
      }
    }
  }

  fun updateRecord(updatedRecord: Record) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        recordRepository.updateRecord(updatedRecord)
      } catch (e: Exception) {
        handleException(e)
      }
    }
  }

  fun deleteRecord(recordToDelete: Record) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        recordRepository.deleteRecord(recordToDelete)
      } catch (e: Exception) {
        handleException(e)
      }
    }
  }

  fun getRecordById(recordId: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        recordRepository.getRecordById(recordId)
      } catch (e: Exception) {
        handleException(e)
      }
    }
  }

  // More functions...
}
