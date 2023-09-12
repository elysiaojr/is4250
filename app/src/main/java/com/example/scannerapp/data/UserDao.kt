package com.example.scannerapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

/*
DAOs are responsible for defining methods that access the DB.
We write queries here!
 */
@Dao
interface UserDao {
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun addUser(user: User): Long

  @Update
  suspend fun update(user: User)

  @Delete
  suspend fun delete(user: User)

  @Query("SELECT * FROM user WHERE userId = :id")
  suspend fun getUserById(id: Int): User?

  @Query("SELECT * FROM user")
  fun getAllUsers(): LiveData<List<User>>
}

