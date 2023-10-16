package com.example.scannerapp.repository

import androidx.lifecycle.LiveData
import com.example.scannerapp.database.dao.ConsumableDao
import com.example.scannerapp.database.entities.Consumable
import com.example.scannerapp.database.entities.UnitOfMeasurement
import com.example.scannerapp.exceptions.ActiveStatusException
import com.example.scannerapp.exceptions.BarcodeIdExistException
import com.example.scannerapp.exceptions.EnumValueDoesNotMatch
import com.example.scannerapp.exceptions.FieldCannotBeEmptyException
import com.example.scannerapp.exceptions.InsufficientQuantityException

/*
Repositories are responsible for abstracting the source of data for your app.
Most business logic are here.
 */
class ConsumableRepository(private val consumableDao: ConsumableDao) {
  val getAllConsumables: LiveData<List<Consumable>> = consumableDao.getAllConsumables()

  /*
   * Adds a new consumable to the database after validation.
   * Checks for uniqueness of barcode ID.
   */
  suspend fun addConsumable(consumable: Consumable) {

    val trimmedBarcodeId = consumable.barcodeId.trim()

    if (consumableDao.getConsumableByBarcodeId(trimmedBarcodeId) != null) {
      throw BarcodeIdExistException("Barcode ID already exists")
    }

    validateConsumable(consumable)

    consumableDao.insert(consumable)
  }

  /*
   * Updates an existing consumable in the database after validation.
   * Checks for uniqueness of barcode ID.
   */
  suspend fun updateConsumable(consumable: Consumable) {
    val trimmedBarcodeId = consumable.barcodeId.trim()

    val existingConsumable = consumableDao.getConsumableByBarcodeId(trimmedBarcodeId)
    // Make sure the (old) consumable we are editing has the same barcode id as the (new) consumable
    if (existingConsumable != null && existingConsumable.consumableId != consumable.consumableId) {
      throw BarcodeIdExistException("Barcode ID already exists for another consumable")
    }

    validateConsumable(consumable)

    consumableDao.update(consumable)
  }

  suspend fun deleteConsumable(consumable: Consumable) {
    consumableDao.delete(consumable)
  }


  fun getConsumableById(consumableId: Int): Consumable {
    return consumableDao.getConsumableById(consumableId)
  }

  fun getConsumableByBarcodeId(barcodeId: String): Consumable {
    return consumableDao.getConsumableByBarcodeId(barcodeId)
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
    if (consumable.barcodeId.trim().isEmpty()) {
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
