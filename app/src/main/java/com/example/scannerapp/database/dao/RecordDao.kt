package com.example.scannerapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.scannerapp.database.entities.Record

/*
DAOs are responsible for defining methods that access the DB.
We write queries here!
 */
@Dao
interface RecordDao {
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun addRecord(record: Record): Long

  @Update
  suspend fun update(record: Record)

  @Delete
  suspend fun delete(record: Record)

  @Query("SELECT * FROM record WHERE recordId = :id")
  suspend fun getRecordById(id: Int): Record?

  @Query("SELECT * FROM record")
  fun getAllRecords(): LiveData<List<Record>>
}

