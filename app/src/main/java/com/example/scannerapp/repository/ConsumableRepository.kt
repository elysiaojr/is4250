package com.example.scannerapp.repository

import androidx.lifecycle.LiveData
import com.example.scannerapp.database.dao.ConsumableDao
import com.example.scannerapp.database.entities.Consumable
import com.example.scannerapp.database.entities.User

/*
Repositories are responsible for abstracting the source of data for your app.
Most business logic are here.
 */
class ConsumableRepository(private val consumableDao: ConsumableDao) {
  val getAllConsumables: LiveData<List<Consumable>> = consumableDao.getAllConsumables()

  suspend fun addConsumable(consumable: Consumable) {
    consumableDao.insert(consumable)
  }

  suspend fun updateConsumable(consumable: Consumable) {
    consumableDao.update(consumable)
  }

  suspend fun deleteConsumable(consumable: Consumable) {
    consumableDao.delete(consumable)
  }


  fun getConsumableById(consumableId: Int): Consumable {
    return consumableDao.getConsumableById(consumableId)
  }

  // More functions...
}
