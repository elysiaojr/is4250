package com.example.scannerapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.scannerapp.database.AppDatabase
import com.example.scannerapp.database.entities.Record
import com.example.scannerapp.exceptions.InsufficientQuantityException
import com.example.scannerapp.exceptions.InvalidRecordTypeException
import com.example.scannerapp.repository.BatchDetailsRepository
import com.example.scannerapp.repository.RecordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecordViewModel(application: Application) : AndroidViewModel(application) {

  val allRecords: LiveData<List<Record>>
  private val recordRepository: RecordRepository
  val errorMessage = MutableLiveData<String>() // To pass error message to UI

  init {
    val recordDao = AppDatabase.getDatabase(application).recordDao()
    val batchDetailsDao = AppDatabase.getDatabase(application).batchDetailsDao()
    recordRepository = RecordRepository(recordDao, batchDetailsDao)
    allRecords = recordRepository.getAllRecords
  }

  fun addRecord(record: Record) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        recordRepository.addRecord(record)
      } catch (e: InsufficientQuantityException) {
        errorMessage.postValue(e.message)
      } catch (e: InvalidRecordTypeException) {
        errorMessage.postValue(e.message)
      } catch (e: Exception) {
        errorMessage.postValue("An unknown error occurred.")
      }
    }
  }

  fun updateRecord(updatedRecord: Record) {
    viewModelScope.launch(Dispatchers.IO) {
      recordRepository.updateRecord(updatedRecord)
    }
  }

  fun deleteRecord(recordToDelete: Record) {
    viewModelScope.launch(Dispatchers.IO) {
      recordRepository.deleteRecord(recordToDelete)
    }
  }

  fun getRecordById(recordId: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      recordRepository.getRecordById(recordId)
    }
  }

  // More functions...
}
