package com.example.scannerapp.repository

import androidx.lifecycle.LiveData
import com.example.scannerapp.database.dao.BatchDetailsDao
import com.example.scannerapp.database.entities.BatchDetails
import com.example.scannerapp.database.entities.UnitOfMeasurement
import com.example.scannerapp.exceptions.ActiveStatusException
import com.example.scannerapp.exceptions.BatchNumberExistException
import com.example.scannerapp.exceptions.ExpiryDateBeforeCreateDate
import com.example.scannerapp.exceptions.FieldCannotBeEmptyException
import com.example.scannerapp.exceptions.InsufficientQuantityException
import com.example.scannerapp.exceptions.RemainingQuantityExceedsReceivedQuantityException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/*
Repositories are responsible for abstracting the source of data for your app.
Most business logic are here.
 */
class BatchDetailsRepository(private val batchDetailsDao: BatchDetailsDao) {
  val getAllBatchDetails: LiveData<List<BatchDetails>> =
    batchDetailsDao.getAllBatchDetailsByLatestDate()
//  val getAllBatchDetailsByExpiryDate: LiveData<List<BatchDetails>> =
//    batchDetailsDao.getAllBatchDetailsByExpiryDate()
//  val getAllBatchDetailsByConsumableNameAsc: LiveData<List<BatchDetails>> =
//    batchDetailsDao.getAllBatchDetailsByConsumableNameAsc()
//  val getAllBatchDetailsByConsumableNameDesc: LiveData<List<BatchDetails>> =
//    batchDetailsDao.getAllBatchDetailsByConsumableNameDesc()

  suspend fun addBatchDetails(batchDetails: BatchDetails) {
    val trimmedBatchNumber = batchDetails.batchNumber.trim()

    if (batchDetailsDao.getBatchDetailByBatchNumber(trimmedBatchNumber) != null) {
      throw BatchNumberExistException("Batch number already exists")
    }

    validateBatchDetails(batchDetails)
    batchDetailsDao.insert(batchDetails)
  }

  suspend fun updateBatchDetails(batchDetails: BatchDetails) {
    val trimmedBatchNumber = batchDetails.batchNumber.trim()

    val existingBatchDetails = batchDetailsDao.getBatchDetailByBatchNumber(trimmedBatchNumber)

    // Make sure the (old) consumable we are editing has the same barcode id as the (new) consumable
    if (existingBatchDetails != null && existingBatchDetails.batchId != batchDetails.batchId) {
      throw BatchNumberExistException("Batch number already exists for another Batch")
    }

    validateBatchDetails(batchDetails)
    batchDetailsDao.update(batchDetails)
  }

  suspend fun deleteBatchDetails(batchDetails: BatchDetails) {
    batchDetailsDao.delete(batchDetails)
  }

  suspend fun countOfBatchDetailsByBatchNumber(batchNumber: String): Int {
    return batchDetailsDao.countOfBatchDetailsByBatchNumber(batchNumber)
  }

  suspend fun getBatchDetailsById(batchId: Int): BatchDetails {
    return batchDetailsDao.getBatchDetailById(batchId)
  }

  suspend fun getBatchDetailsByBatchNumber(batchNumber: String): BatchDetails {
    return batchDetailsDao.getBatchDetailByBatchNumber(batchNumber)
  }

  suspend fun getBatchIdByBatchNumber(batchNumber: String): Int {
    return batchDetailsDao.getBatchIdByBatchNumber(batchNumber)
  }

  suspend fun getBatchDetailUOM(consumableId: Int): UnitOfMeasurement {
    return batchDetailsDao.getBatchDetailUOM(consumableId)
  }

  suspend fun getBatchDetailConsumableName(consumableId: Int): String {
    return batchDetailsDao.getBatchDetailConsumableName(consumableId)
  }

  suspend fun getBatchDetailConsumableBrand(consumableId: Int): String {
    return batchDetailsDao.getBatchDetailConsumableBrand(consumableId)
  }

  suspend fun getBatchDetailConsumableType(consumableId: Int): String {
    return batchDetailsDao.getBatchDetailConsumableType(consumableId)
  }

  suspend fun getBatchDetailConsumableSize(consumableId: Int): String {
    return batchDetailsDao.getBatchDetailConsumableSize(consumableId)
  }

  suspend fun getBatchDetailConsumableNameByBatchNumber(batchNumber: String): String {
    return batchDetailsDao.getBatchDetailConsumableNameByBatchNumber(batchNumber)
  }

  suspend fun getBatchDetailConsumableBrandByBatchNumber(batchNumber: String): String {
    return batchDetailsDao.getBatchDetailConsumableBrandByBatchNumber(batchNumber)
  }

  suspend fun getBatchDetailConsumableTypeByBatchNumber(batchNumber: String): String {
    return batchDetailsDao.getBatchDetailConsumableTypeByBatchNumber(batchNumber)
  }

  suspend fun getBatchDetailConsumableSizeByBatchNumber(batchNumber: String): String {
    return batchDetailsDao.getBatchDetailConsumableSizeByBatchNumber(batchNumber)
  }

  private fun validateBatchDetails(batchDetails: BatchDetails) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy") // Assuming this is your date format

    val createDate = LocalDate.parse(batchDetails.createDate, formatter)
    val expiryDate = LocalDate.parse(batchDetails.expiryDate, formatter)
    if (expiryDate.isBefore(createDate)) {
      throw ExpiryDateBeforeCreateDate("Expiry Date must not be before today")
    }

    if (batchDetails.batchNumber.trim().isEmpty()) {
      throw FieldCannotBeEmptyException("Batch Number field cannot be empty")
    }
    if (batchDetails.createDate.trim().isEmpty()) {
      throw FieldCannotBeEmptyException("Create Date field cannot be empty")
    }
    if (batchDetails.expiryDate.trim().isEmpty()) {
      throw FieldCannotBeEmptyException("Expiry Date field cannot be empty")
    }
    if (batchDetails.batchReceivedQuantity <= 0) {
      throw InsufficientQuantityException("Received Quantity must be at least 1")
    }
    if (batchDetails.batchRemainingQuantity < 0) {
      throw InsufficientQuantityException("Remaining Quantity cannot be negative")
    }
    // Check batchRemainingQuantity against batchReceivedQuantity
    if (batchDetails.batchRemainingQuantity > batchDetails.batchReceivedQuantity) {
      throw RemainingQuantityExceedsReceivedQuantityException("Remaining Quantity cannot be greater than Received Quantity")
    }
    if (batchDetails.isActive != 0 && batchDetails.isActive != 1) {
      throw ActiveStatusException("Active field can only be active (1) or not active (0)")
    }
  }


  // More functions...
}

