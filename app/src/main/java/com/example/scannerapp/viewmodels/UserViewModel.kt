package com.example.scannerapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.scannerapp.database.AppDatabase
import com.example.scannerapp.database.entities.User
import com.example.scannerapp.exceptions.InvalidUserNameException
import com.example.scannerapp.exceptions.InvalidUserStatusException
import com.example.scannerapp.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/*
The ViewModel is a part of the Android Architecture Components.
It's designed to store and manage UI-related data so that the data survives configuration changes such as screen rotations.
 */
class UserViewModel(application: Application) : AndroidViewModel(application) {

  val allUsers: LiveData<List<User>>
  private val userRepository: UserRepository
  val errorLiveData = MutableLiveData<String>()

  init {
    val userDao = AppDatabase.getDatabase(application).userDao()
    userRepository = UserRepository(userDao)
    allUsers = userRepository.getAllUsers
  }

  private fun handleException(e: Exception) {
    when (e) {
      is InvalidUserNameException -> errorLiveData.postValue(e.message)
      is InvalidUserStatusException -> errorLiveData.postValue(e.message)
      else -> errorLiveData.postValue("An unknown error occurred.")
    }
  }

  fun addUser(user: User) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        userRepository.addUser(user)
      } catch (e: Exception) {
        handleException(e)
      }
    }
  }

  fun updateUser(updatedUser: User) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        userRepository.updateUser(updatedUser)
      } catch (e: Exception) {
        handleException(e)
      }
    }
  }
  
  fun deleteUser(userToDelete: User) {
    viewModelScope.launch(Dispatchers.IO) {
      userRepository.deleteUser(userToDelete)
    }
  }

  fun getUserById(userId: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      userRepository.getUserById(userId)
    }
  }

  // More functions...
}
