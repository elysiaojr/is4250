package com.example.scannerapp.repository

import androidx.lifecycle.LiveData
import com.example.scannerapp.database.dao.UserDao
import com.example.scannerapp.database.entities.User
import com.example.scannerapp.exceptions.InvalidUserNameException
import com.example.scannerapp.exceptions.InvalidUserStatusException

/*
Repositories are responsible for abstracting the source of data for your app.
Most business logic are here.
 */
class UserRepository(private val userDao: UserDao) {

  val getAllUsers: LiveData<List<User>> = userDao.getAllUsers()

  suspend fun addUser(user: User) {
    validateUser(user)
    userDao.insert(user)
  }

  suspend fun updateUser(user: User) {
    validateUser(user)
    userDao.update(user)
  }

  suspend fun deleteUser(user: User) {
    userDao.delete(user)
  }

  suspend fun getUserById(userId: Int): User {
    return userDao.getUserById(userId)
  }

  private fun validateUser(user: User) {
    if (user.name.trim().isEmpty()) {
      throw InvalidUserNameException("User name cannot be empty")
    }
    if (user.status != 0 && user.status != 1) {
      throw InvalidUserStatusException("Invalid status. Status can only be 0 (inactive) or 1 (active).")
    }
  }

  // More functions...
}

