package com.example.scannerapp.repository

import androidx.lifecycle.LiveData
import com.example.scannerapp.database.dao.UserDao
import com.example.scannerapp.database.entities.User

/*
Repositories are responsible for abstracting the source of data for your app.
Most business logic are here.
 */
class UserRepository(private val userDao: UserDao) {

  val getAllUsers: LiveData<List<User>> = userDao.getAllUsers()

  suspend fun addUser(user: User) {
    userDao.insert(user)
  }

  // For soft deletion, use this
  suspend fun updateUser(user: User) {
    userDao.update(user)
  }

  suspend fun deleteUser(user: User) {
    userDao.delete(user)
  }

  suspend fun getUserById(user: User) {
    userDao.getUserById(user.userId)
  }

  // More functions...
}

