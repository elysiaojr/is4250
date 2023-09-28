package com.example.scannerapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.scannerapp.database.entities.BatchDetails

/*
DAOs are responsible for defining methods that access the DB.
We write queries here!
 */
@Dao
interface BatchDetailsDao {
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insert(batchDetail: BatchDetails)

  @Update
  suspend fun update(batchDetail: BatchDetails)

  @Delete
  suspend fun delete(batchDetail: BatchDetails)

  @Query("SELECT * FROM batch_details WHERE batchId = :id")
  suspend fun getBatchDetailsById(id: Int): BatchDetails

  @Query("SELECT * FROM batch_details")
  fun getAllBatchDetails(): LiveData<List<BatchDetails>>
}

