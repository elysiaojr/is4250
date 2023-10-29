package com.example.scannerapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.scannerapp.database.dao.ConsumableDao
import com.example.scannerapp.database.entities.Consumable
import com.example.scannerapp.database.entities.UnitOfMeasurement
import com.example.scannerapp.exceptions.ActiveStatusException
import com.example.scannerapp.exceptions.ItemCodeExistException
import com.example.scannerapp.exceptions.EnumValueDoesNotMatch
import com.example.scannerapp.exceptions.FieldCannotBeEmptyException
import com.example.scannerapp.exceptions.InsufficientQuantityException

/*
Repositories are responsible for abstracting the source of data for your app.
Most business logic are here.
 */
class ConsumableRepository(private val consumableDao: ConsumableDao) {
  val getAllConsumables: LiveData<List<Consumable>> = consumableDao.getAllConsumables()

  val getAllActiveConsumables: LiveData<List<Consumable>> = consumableDao.getAllActiveConsumables()

  // LiveData to hold the remaining quantities
  private val remainingQuantitiesLiveData: MutableLiveData<Map<Int, Int>> = MutableLiveData()

  /*
   * Adds a new consumable to the database after validation.
   * Checks for uniqueness of barcode ID.
   */
  suspend fun addConsumable(consumable: Consumable) {

    val trimmedItemCode = consumable.itemCode.trim()

    if (consumableDao.getConsumableByItemCode(trimmedItemCode) != null) {
      throw ItemCodeExistException("Item Code already exists")
    }

    validateConsumable(consumable)

    consumableDao.insert(consumable)
    updateRemainingQuantitiesLiveData()
  }

  fun getRemainingQuantitiesLiveData(): LiveData<Map<Int, Int>> = remainingQuantitiesLiveData

  private fun updateRemainingQuantitiesLiveData() {
    val consumables = getAllConsumables.value.orEmpty()
    val remainingQuantitiesMap = mutableMapOf<Int, Int>()

    for (consumable in consumables) {
      val consumableId = consumable.consumableId
      val remainingQuantity = getAllBatchesQuantityRemaining(consumableId)
      remainingQuantitiesMap[consumableId] = remainingQuantity
    }

    remainingQuantitiesLiveData.postValue(remainingQuantitiesMap)
  }

  /*
   * Updates an existing consumable in the database after validation.
   * Checks for uniqueness of barcode ID.
   */
  suspend fun updateConsumable(consumable: Consumable) {
    val trimmedItemCode = consumable.itemCode.trim()

    val existingConsumable = consumableDao.getConsumableByItemCode(trimmedItemCode)
    // Make sure the (old) consumable we are editing has the same barcode id as the (new) consumable
    if (existingConsumable != null && existingConsumable.consumableId != consumable.consumableId) {
      throw ItemCodeExistException("Barcode ID already exists for another consumable")
    }

    validateConsumable(consumable)

    consumableDao.update(consumable)
    updateRemainingQuantitiesLiveData()
  }

  suspend fun deleteConsumable(consumable: Consumable) {
    consumableDao.delete(consumable)
    updateRemainingQuantitiesLiveData()
  }


  fun getConsumableById(consumableId: Int): Consumable {
    return consumableDao.getConsumableById(consumableId)
  }

  fun getConsumableByItemCode(itemCode: String): Consumable {
    return consumableDao.getConsumableByItemCode(itemCode)
  }

  fun getAllBatchesQuantityRemaining(consumableId: Int): Int {
    return consumableDao.getAllBatchesQuantityRemaining(consumableId)
  }

  /*
   * Validates a consumable object before adding or updating in the database.
   * Checks for required fields, valid enum values, and other constraints.
   */
  private fun validateConsumable(consumable: Consumable) {
    if (consumable.consumableName.trim().isEmpty()) {
      throw FieldCannotBeEmptyException("Consumable Name field cannot be empty")
    }
    if (consumable.consumableBrand.trim().isEmpty()) {
      throw FieldCannotBeEmptyException("Consumable Brand field cannot be empty")
    }
    if (consumable.itemCode.trim().isEmpty()) {
      throw FieldCannotBeEmptyException("Barcode ID field cannot be empty")
    }
    if (consumable.unitOfMeasurement !in UnitOfMeasurement.values()) {
      throw EnumValueDoesNotMatch("Invalid unit of measurement")
    }
    if (consumable.minimumQuantity <= 0) {
      throw InsufficientQuantityException("Minimum Quantity must be at least 1")
    }
    if (consumable.isActive != 0 && consumable.isActive != 1) {
      throw ActiveStatusException("Active field can only be active (1) or not active (0)")
    }
  }

  // More functions...
}
