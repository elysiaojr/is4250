package com.example.scannerapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.scannerapp.database.entities.Consumable

/*
DAOs are responsible for defining methods that access the DB.
We write queries here!
 */
@Dao
interface ConsumableDao {
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insert(consumable: Consumable)

  @Update
  suspend fun update(consumable: Consumable)

  @Delete
  suspend fun delete(consumable: Consumable)

  @Query("SELECT * FROM consumable WHERE consumableId = :id")
  fun getConsumableById(id: Int): Consumable

  @Query("SELECT * FROM consumable WHERE itemCode = :itemCode")
  fun getConsumableByItemCode(itemCode: String): Consumable

  @Query("SELECT * FROM consumable")
  fun getAllConsumables(): LiveData<List<Consumable>>

  @Query("SELECT SUM(b.batchRemainingQuantity) AS currentQuantity FROM consumable c JOIN batch_details b ON c.consumableId = b.consumableId WHERE c.consumableId = :consumableId")
  fun getAllBatchesQuantityRemaining(consumableId: Int): Int
}

