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
interface ConsumableDao {
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun addConsumable(consumable: Consumable): Long

  @Update
  suspend fun update(consumable: Consumable)

  @Delete
  suspend fun delete(consumable: Consumable)

  @Query("SELECT * FROM consumable WHERE consumableId = :id")
  suspend fun getConsumableById(id: Int): Consumable?

  @Query("SELECT * FROM consumable")
  fun getAllConsumables(): LiveData<List<Consumable>>
}

