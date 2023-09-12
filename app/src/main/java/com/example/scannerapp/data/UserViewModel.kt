package com.example.scannerapp.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/*
The ViewModel is a part of the Android Architecture Components.
It's designed to store and manage UI-related data so that the data survives configuration changes such as screen rotations.
 */
class UserViewModel(application: Application) : AndroidViewModel(application) {

  val allUsers: LiveData<List<User>>
  private val userRepository: UserRepository

  init {
    val userDao = AppDatabase.getDatabase(application).userDao()
    userRepository = UserRepository(userDao)
    allUsers = userRepository.getAllUsers
  }

  fun addUser(user: User) {
    viewModelScope.launch(Dispatchers.IO) {
      userRepository.addUser(user)
    }
  }

  // More functions...
}

