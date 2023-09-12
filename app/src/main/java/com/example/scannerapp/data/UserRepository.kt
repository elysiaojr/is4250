package com.example.scannerapp.data

import androidx.lifecycle.LiveData

/*
Repositories are responsible for abstracting the source of data for your app.
Most business logic are here.
 */
class UserRepository(private val userDao: UserDao) {

  val getAllUsers: LiveData<List<User>> = userDao.getAllUsers()

  suspend fun addUser(user: User) {
    userDao.addUser(user)
  }

  // More functions...
}

