package com.example.scannerapp.data

import androidx.lifecycle.LiveData

/*
Repositories are responsible for abstracting the source of data for your app.
Most business logic are here.
 */
class ConsumableRepository(private val consumableDao: ConsumableDao) {
  val getAllConsumables: LiveData<List<Consumable>> = consumableDao.getAllConsumables()

  suspend fun addConsumable(consumable: Consumable) {
    consumableDao.addConsumable(consumable)
  }

  suspend fun getConsumable(consumableId: Int) {
    consumableDao.getConsumableById(consumableId)
  }

  // More functions...
}
