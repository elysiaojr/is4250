package com.example.scannerapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.scannerapp.database.AppDatabase
import com.example.scannerapp.database.entities.User
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

  fun getUserById(userId: Int) {

  }

  fun updateUser(updatedUser: User) {

  }

  // More functions...
}

